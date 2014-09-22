package com.palmcel.parenting.feed;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.cache.FeedCache;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataSource;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.db.DatabaseContract;
import com.palmcel.parenting.db.DbHelper;
import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.FeedPostBuilder;
import com.palmcel.parenting.model.LoadFeedParams;
import com.palmcel.parenting.model.LoadFeedResult;
import com.palmcel.parenting.model.LoadFeedResultEvent;
import com.palmcel.parenting.model.PostPublicity;
import com.palmcel.parenting.model.PostStatus;
import com.palmcel.parenting.model.PostType;

import java.util.concurrent.Callable;
import de.greenrobot.event.EventBus;

import static com.palmcel.parenting.db.DatabaseContract.PostEntry;
import static com.palmcel.parenting.db.DatabaseContract.FeedEntry;

/**
 * Singleton class that loads feed
 */
public class LoadFeedManager {

    private static final String TAG = "LoadFreeManager";
    private static final int DEFAULT_MAX_FETCH = 20;

    private static LoadFeedManager INSTANCE = new LoadFeedManager();

    private ListenableFuture<LoadFeedResult> mLoadFeedFuture;

    public void loadFeed() {
        loadFeed(new LoadFeedParams(0, DEFAULT_MAX_FETCH, DataFreshnessParam.CACHE_OK));
    }

    public void loadFeed(DataFreshnessParam dataFreshnessParam) {
        loadFeed(new LoadFeedParams(0, DEFAULT_MAX_FETCH, dataFreshnessParam));
    }

    public void loadFeed(final LoadFeedParams loadFeedParams) {
        if (mLoadFeedFuture != null) {
            Log.d(TAG, "loadFeed was skipped.");
            return;
        } else {
            Log.d(TAG, "In loadFeed");
        }

        final boolean isLoadMore = loadFeedParams.timeMsInsertedSince > 0;

        if (loadFeedParams.dataFreshnessParam == DataFreshnessParam.CACHE_OK &&
                FeedCache.getInstance().isUpToDate() &&
                !FeedCache.getInstance().isEmpty()) {
            LoadFeedResult result = LoadFeedResult.successResult(
                    FeedCache.getInstance().getCachedFeed(),
                    DataSource.MEMORY_CACHE);
            // Update feed listview with memory cache data
            EventBus.getDefault().post(
                    new LoadFeedResultEvent(loadFeedParams, result));
            Log.d(TAG, "To display feed from memory cache");
            return;
        }

        mLoadFeedFuture = ExecutorUtil.execute(new Callable<LoadFeedResult>() {
            @Override
            public LoadFeedResult call() throws Exception {
                if (FeedCache.getInstance().isEmpty()) {
                    LoadFeedResult dbResult = loadFeedFromDb(loadFeedParams);
                    if (!dbResult.isEmpty() && !isLoadMore) {
                        FeedCache.getInstance().updateCacheFromDb(dbResult.feedPosts);
                        // Update feed listview with database data
                        EventBus.getDefault().post(
                                new LoadFeedResultEvent(loadFeedParams, dbResult));
                        Log.d(TAG, "To display feed from db");
                    }
                }

                // TODO: maybe add logic to display feed from db for CACHE_OK. Need to check
                // staleness of db data.

                // Load from server
                FeedHandler feedHandler = new FeedHandler();
                final ImmutableList<FeedPost> feedFromServer =
                        feedHandler.getFeedPostFromServer(
                                "pkdebug", // TODO
                                loadFeedParams.timeMsInsertedSince,
                                loadFeedParams.maxToFetch,
                                FeedCache.getInstance().getLargestInsertTime()
                        );

                if (loadFeedParams.timeMsInsertedSince == 0) {
                    FeedCache.getInstance().updateCacheFromServer(feedFromServer);
                } else {
                    // Load-more case
                    FeedCache.getInstance().updateCacheFromServer(
                            loadFeedParams.timeMsInsertedSince,
                            feedFromServer);
                }
                // Update feed_post table with feedFromServer on a separated thread
                ExecutorUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (loadFeedParams.timeMsInsertedSince == 0) {
                            updateFeedPostTable(feedFromServer);
                        } else {
                            // Load-more case
                            updateFeedPostTable(loadFeedParams.timeMsInsertedSince,feedFromServer);
                        }
                    }
                });

                LoadFeedResult result = LoadFeedResult.successResult(
                        FeedCache.getInstance().getCachedFeed(),
                        DataSource.SERVER);

                Log.d(TAG, "To display feed from server");
                return result;
            }
        });

        Futures.addCallback(mLoadFeedFuture, new FutureCallback<LoadFeedResult>() {
            @Override
            public void onSuccess(LoadFeedResult result) {
                Log.d(TAG, "mLoadFeedFuture succeeded");
                mLoadFeedFuture = null;
                EventBus.getDefault().post(new LoadFeedResultEvent(loadFeedParams, result));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("LoadFreeManager", "mLoadFeedFuture failed", t);
                mLoadFeedFuture = null;
                LoadFeedResult result = LoadFeedResult.errorResult(t);
                EventBus.getDefault().post(new LoadFeedResultEvent(loadFeedParams, result));
            }
        });
    }

    private LoadFeedResult loadFeedFromDb(LoadFeedParams loadFeedParams) {
        Log.d(TAG, "In loadFeedFromDb");
        SQLiteDatabase db = DbHelper.getDb();

        String selectFields = DatabaseContract.FEED_COLUMNS_STRING;

        String query =
            "SELECT " + selectFields + " " +
            "FROM " + FeedEntry.TABLE_NAME + " " +
            "ORDER BY " + FeedEntry.COLUMN_TIME_INSERTED + " DESC " +
            "LIMIT " + 100; //TODO

        Cursor cursor = db.rawQuery(query, null);

        ImmutableList.Builder<FeedPost> listBuilder = ImmutableList.builder();

        while (cursor.moveToNext()) {
            FeedPostBuilder feedPostBuilder = new FeedPostBuilder();

            feedPostBuilder.setPostId(cursor.getString(0));
            feedPostBuilder.setTimeMsInserted(cursor.isNull(1) ? 0 : cursor.getLong(1));
            feedPostBuilder.setUserId(cursor.getString(2));
            String postTypeStr = cursor.getString(3);
            feedPostBuilder.setPostType(postTypeStr == null ? null : PostType.valueOf(postTypeStr));
            feedPostBuilder.setCategory(cursor.getString(4));
            feedPostBuilder.setMessage(cursor.getString(5));
            feedPostBuilder.setPictureUrl(cursor.getString(6));
            feedPostBuilder.setExternalLinkUrl(cursor.getString(7));
            feedPostBuilder.setExternalLinkImageUrl(cursor.getString(8));
            feedPostBuilder.setExternalLinkCaption(cursor.getString(9));
            feedPostBuilder.setExternalLinkSummary(cursor.getString(10));
            feedPostBuilder.setProductBarCode(cursor.getString(11));
            String publicityStr = cursor.getString(12);
            feedPostBuilder.setPublicity(
                    publicityStr == null ? null : PostPublicity.valueOf(publicityStr));
            feedPostBuilder.setLikes(cursor.isNull(13) ? 0 : cursor.getInt(13));
            feedPostBuilder.setComments(cursor.isNull(14) ? 0 : cursor.getInt(14));
            feedPostBuilder.setIsAnonymous(
                    cursor.isNull(15) || cursor.getInt(15) == 0 ? false : true);
            String statusStr = cursor.getString(16);
            feedPostBuilder.setPostStatus(statusStr == null ? null : PostStatus.valueOf(statusStr));
            feedPostBuilder.setTimeMsCreated(cursor.isNull(17) ? 0 : cursor.getLong(17));
            feedPostBuilder.setTimeMsEdited(cursor.isNull(18) ? 0 : cursor.getLong(18));
            feedPostBuilder.setTimeMsChangeToSurface(cursor.isNull(19) ? 0 : cursor.getLong(19));

            listBuilder.add(feedPostBuilder.build());
        }

        return LoadFeedResult.successResult(listBuilder.build(), DataSource.DATABASE);
    }

    /**
     * Update feed_post table with feed loaded from server
     * @param feedFromServer feed from server. The feed starts from the latest post in the feed. It
     *                       is not middle portion in the feed.
     */
    private void updateFeedPostTable(ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateFeedPostTable, feedFromServer=" + feedFromServer.size());

        if (feedFromServer.isEmpty()) {
            return;
        }

        FeedPost lastInServerFeed = feedFromServer.get(feedFromServer.size() - 1);

        SQLiteDatabase db = DbHelper.getDb();
        db.beginTransaction();

        try {
            // Check whether feed_post table contains lastInServerFeed
            if (feedPostTableContains(lastInServerFeed.postId, lastInServerFeed.timeMsInserted)) {
                Log.d(TAG, "updateFeedPostTable, feed_post table contains post (" +
                        lastInServerFeed.postId + ", " + lastInServerFeed.timeMsInserted + ")");

                // Delete posts before lastInServerFeed.timeMsInserted in feed_post table
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        FeedEntry.COLUMN_TIME_INSERTED + " >= ?",
                        new String[]{Long.toString(lastInServerFeed.timeMsInserted)}
                        );
            } else {
                Log.d(TAG, "updateFeedPostTable, feed_post table doesn't contain post (" +
                        lastInServerFeed.postId + ", " + lastInServerFeed.timeMsInserted + ")");
                // Clear feed_post table
                db.delete(DatabaseContract.FeedEntry.TABLE_NAME, null, null);
            }

            // Insert feedFromServer into feed_post table
            insertIntoFeedPostTable(db, feedFromServer);

            db.setTransactionSuccessful();
            Log.d(TAG, "updateFeedPostTable succeeded");
        } catch (Exception ex) {
            Log.e(TAG, "Exception in updateFeedPostTable", ex);
        } finally {
            db.endTransaction();
        }
    }


    /**
     * Update feed_post table with the load-more feed from server
     * @param timeMsInsertedSince the load-more feed is loaded from timeMsInsertedSince and older
     *                            than it.
     * @param feedFromServer feed from server sorted by timeMsInserted in DESC order.
     *                       The feed is a result of load more. That is, it
     *                       doesn't start with the latest post in the feed at server.
     */
    private void updateFeedPostTable(
            long timeMsInsertedSince,
            ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateFeedPostTable for load-more, timeMsInsertedSince=" +
                timeMsInsertedSince + ", feedFromServer=" + feedFromServer.size());

        if (feedFromServer.isEmpty()) {
            Log.d(TAG, "updateFeedPostTable for load-more, empty feed from server");
            return;
        }

        FeedPost firstInServerFeed = feedFromServer.get(0);

        if (!feedPostTableContains(firstInServerFeed.postId, firstInServerFeed.timeMsInserted)) {
            Log.d(TAG, "updateFeedPostTable for load-more, feed_post table doesn't contain " +
                    firstInServerFeed.timeMsInserted);
            return;
        }

        FeedPost lastInServerFeed = feedFromServer.get(feedFromServer.size() - 1);

        SQLiteDatabase db = DbHelper.getDb();
        db.beginTransaction();

        try {
            // Check whether feed_post table contains lastInServerFeed
            if (feedPostTableContains(lastInServerFeed.postId, lastInServerFeed.timeMsInserted)) {
                Log.d(TAG, "updateFeedPostTable for load-more, delete posts from table between " +
                        firstInServerFeed.timeMsInserted +
                        " and " + lastInServerFeed.timeMsInserted);

                // Delete posts between firstInServerFeed.timeMsInserted and
                // lastInServerFeed.timeMsInserted in feed_post table
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        FeedEntry.COLUMN_TIME_INSERTED + " <= ? AND " +
                                FeedEntry.COLUMN_TIME_INSERTED + " >= ?",
                        new String[]{Long.toString(firstInServerFeed.timeMsInserted),
                                Long.toString(lastInServerFeed.timeMsInserted)}
                );
            } else {
                Log.d(TAG, "updateFeedPostTable for load-more, delete posts from table older than "
                        + firstInServerFeed.timeMsInserted);
                // Delete posts order than firstInServerFeed.timeMsInserted in feed_post table.
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        FeedEntry.COLUMN_TIME_INSERTED + " <= ? ",
                        new String[]{Long.toString(firstInServerFeed.timeMsInserted)}
                );
            }

            // Insert feedFromServer into feed_post table
            insertIntoFeedPostTable(db, feedFromServer);

            db.setTransactionSuccessful();
            Log.d(TAG, "updateFeedPostTable succeeded");
        } catch (Exception ex) {
            Log.e(TAG, "Exception in updateFeedPostTable", ex);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Insert feed into feed_post table
     * @param db
     * @param feedPosts
     */
    private void insertIntoFeedPostTable(SQLiteDatabase db, ImmutableList<FeedPost> feedPosts) {
        Log.d(TAG, "In insertIntoFeedPostTable");
        String sql =
            "INSERT INTO " + FeedEntry.TABLE_NAME +
                    " (" + DatabaseContract.FEED_COLUMNS_STRING + ") " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement insert = db.compileStatement(sql);

        for (FeedPost post: feedPosts) {
            insert.bindString(1, post.postId);
            insert.bindLong(2, post.timeMsInserted);
            insert.bindString(3, post.userId);
            if (post.postType == null) {
                insert.bindNull(4);
            } else {
                insert.bindString(4, post.postType.toString());
            }
            if (post.category == null) {
                insert.bindNull(5);
            } else {
                insert.bindString(5, post.category);
            }
            if (post.message == null) {
                insert.bindNull(6);
            } else {
                insert.bindString(6, post.message);
            }
            if (post.pictureUrl == null) {
                insert.bindNull(7);
            } else {
                insert.bindString(7, post.pictureUrl);
            }
            if (post.externalLinkUrl == null) {
                insert.bindNull(8);
            } else {
                insert.bindString(8, post.externalLinkUrl);
            }
            if (post.externalLinkImageUrl == null) {
                insert.bindNull(9);
            } else {
                insert.bindString(9, post.externalLinkImageUrl);
            }
            if (post.externalLinkCaption == null) {
                insert.bindNull(10);
            } else {
                insert.bindString(10, post.externalLinkCaption);
            }
            if (post.externalLinkSummary == null) {
                insert.bindNull(11);
            } else {
                insert.bindString(11, post.externalLinkSummary);
            }
            if (post.productBarCode == null) {
                insert.bindNull(12);
            } else {
                insert.bindString(12, post.productBarCode);
            }
            if (post.publicity == null) {
                insert.bindNull(13);
            } else {
                insert.bindString(13, post.publicity.toString());
            }
            insert.bindLong(14, post.likes);
            insert.bindLong(14, post.likes);
            insert.bindLong(15, post.comments);
            insert.bindLong(16, post.isAnonymous ? 1 : 0);
            if (post.postStatus == null) {
                insert.bindNull(17);
            } else {
                insert.bindString(17, post.postStatus.toString());
            }
            insert.bindLong(18, post.timeMsCreated);
            insert.bindLong(19, post.timeMsEdited);
            insert.bindLong(20, post.timeMsChangeToSurface);
            insert.execute();
        }
    }

    /**
     * @param postId feedPost.postId
     * @param timeMsInserted, feedPost.timeMsInserted
     * @return whether feed_post table contains a post with postId and timeMsInserted
     */
    private boolean feedPostTableContains(String postId, long timeMsInserted) {
        String whereClause =
                PostEntry.COLUMN_POST_ID + "=? AND " + FeedEntry.COLUMN_TIME_INSERTED + "=?";
        Cursor c = DbHelper.getDb().query(
                FeedEntry.TABLE_NAME,
                new String[]{PostEntry.COLUMN_POST_ID},
                whereClause,
                new String[]{postId, Long.toString(timeMsInserted)},
                null,
                null,
                null
        );

        if (c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public static LoadFeedManager getInstance() {
        return INSTANCE;
    }

    /**
     * Load more feed post whose insert time is equal or less than 'timeMsInsertedSince'
     * @param timeMsInsertedSince the insert time of the last post in the feed list view
     */
    public void loadFeedMore(long timeMsInsertedSince) {
        loadFeed(new LoadFeedParams(
                timeMsInsertedSince,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CHECK_SERVER));
    }
}