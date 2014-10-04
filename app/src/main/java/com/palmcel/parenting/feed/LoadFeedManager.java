package com.palmcel.parenting.feed;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.cache.FeedCache;
import com.palmcel.parenting.common.DataLoadCause;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataSource;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.db.DatabaseContract;
import com.palmcel.parenting.db.DbHelper;
import com.palmcel.parenting.db.PostDbHandler;
import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.FeedPostBuilder;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.LoadFeedResultEvent;
import com.palmcel.parenting.model.LoadDataParams;
import com.palmcel.parenting.model.PostPublicity;
import com.palmcel.parenting.model.PostStatus;
import com.palmcel.parenting.model.PostType;

import java.util.concurrent.Callable;
import de.greenrobot.event.EventBus;

import static com.palmcel.parenting.db.DatabaseContract.FeedEntry;

/**
 * Singleton class that loads feed
 */
public class LoadFeedManager {

    private static final String TAG = "LoadFeedManager";
    private static final String LOAD_TAG = "Feed";
    private static final int DEFAULT_MAX_FETCH = 20;

    private static LoadFeedManager INSTANCE = new LoadFeedManager();

    private ListenableFuture<LoadDataResult<FeedPost>> mLoadFeedFuture;

    private LoadFeedManager() {}

    public void loadFeed() {
        loadFeed(new LoadDataParams(
                0,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CACHE_OK,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }

    public void loadFeed(DataFreshnessParam dataFreshnessParam) {
        loadFeed(new LoadDataParams(
                0,
                DEFAULT_MAX_FETCH,
                dataFreshnessParam,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }

    public void loadFeedForced() {
        loadFeed(new LoadDataParams(
                0,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CHECK_SERVER,
                DataLoadCause.USER_REQUEST,
                LOAD_TAG));
    }

    public void loadFeed(final LoadDataParams loadDataParams) {
        if (mLoadFeedFuture != null) {
            Log.d(TAG, "loadFeed was skipped.");
            return;
        } else {
            Log.d(TAG, "In loadFeed");
        }

        final boolean isLoadMore = loadDataParams.timeSince > 0;

        if (loadDataParams.dataFreshnessParam == DataFreshnessParam.CACHE_OK &&
                FeedCache.getInstance().isUpToDate() &&
                !FeedCache.getInstance().isEmpty()) {
            LoadDataResult<FeedPost> result = LoadDataResult.successResult(
                    FeedCache.getInstance().getCachedFeed(),
                    DataSource.MEMORY_CACHE);
            // Update feed listview with memory cache data
            EventBus.getDefault().post(
                    new LoadFeedResultEvent(loadDataParams, result));
            Log.d(TAG, "To display feed from memory cache");
            return;
        }

        mLoadFeedFuture = ExecutorUtil.execute(new Callable<LoadDataResult<FeedPost>>() {
            @Override
            public LoadDataResult<FeedPost> call() throws Exception {
                if (FeedCache.getInstance().isEmpty()) {
                    ImmutableList<FeedPost> dbFeed = loadFeedFromDb(loadDataParams);
                    if (!dbFeed.isEmpty() && !isLoadMore) {
                        ImmutableList<FeedPost> updatedCachedFeed =
                                FeedCache.getInstance().updateCacheFromDb(dbFeed);

                        LoadDataResult<FeedPost> dbResult = LoadDataResult.successResult(
                                updatedCachedFeed, DataSource.DATABASE);
                        // Update feed listview with database data
                        EventBus.getDefault().post(
                                new LoadFeedResultEvent(loadDataParams, dbResult));
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
                                loadDataParams.timeSince,
                                loadDataParams.maxToFetch,
                                FeedCache.getInstance().getLargestSortTime()
                        );

                ImmutableList<FeedPost> recentCachedFeed;
                if (loadDataParams.timeSince == 0) {
                    recentCachedFeed = FeedCache.getInstance().updateCacheFromServer(feedFromServer);
                } else {
                    // Load-more case
                    recentCachedFeed = FeedCache.getInstance().updateCacheFromServer(
                            loadDataParams.timeSince,
                            feedFromServer);
                }
                // Update feed_post table with feedFromServer on a separated thread
                ExecutorUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (loadDataParams.timeSince == 0) {
                            PostDbHandler.getInstance().updateFeedPostTable(feedFromServer);
                        } else {
                            // Load-more case
                            PostDbHandler.getInstance().updateFeedPostTable(
                                    loadDataParams.timeSince,feedFromServer);
                        }
                    }
                });

                LoadDataResult<FeedPost> result = LoadDataResult.successResult(
                        recentCachedFeed,
                        DataSource.SERVER);

                Log.d(TAG, "To display feed from server");
                return result;
            }
        });

        Futures.addCallback(mLoadFeedFuture, new FutureCallback<LoadDataResult<FeedPost>>() {
            @Override
            public void onSuccess(LoadDataResult<FeedPost> result) {
                Log.d(TAG, "mLoadFeedFuture succeeded");
                mLoadFeedFuture = null;
                EventBus.getDefault().post(new LoadFeedResultEvent(loadDataParams, result));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("LoadFreeManager", "mLoadFeedFuture failed", t);
                mLoadFeedFuture = null;
                LoadDataResult result = LoadDataResult.errorResult(t);
                EventBus.getDefault().post(new LoadFeedResultEvent(loadDataParams, result));
            }
        });
    }

    private ImmutableList<FeedPost> loadFeedFromDb(LoadDataParams loadDataParams) {
        Log.d(TAG, "In loadFeedFromDb");
        SQLiteDatabase db = DbHelper.getDb();

        String selectFields = DatabaseContract.FEED_COLUMNS_STRING;

        String query =
            "SELECT " + selectFields + " " +
            "FROM " + FeedEntry.TABLE_NAME + " " +
            "ORDER BY " + FeedEntry.COLUMN_TIME_SORT + " DESC " +
            "LIMIT " + 100; //TODO

        Cursor cursor = db.rawQuery(query, null);

        ImmutableList.Builder<FeedPost> listBuilder = ImmutableList.builder();

        while (cursor.moveToNext()) {
            FeedPostBuilder feedPostBuilder = new FeedPostBuilder();

            feedPostBuilder.setPostId(cursor.getString(0));
            feedPostBuilder.setTimeToSort(cursor.isNull(1) ? 0 : cursor.getLong(1));
            feedPostBuilder.setIsLiked(
                    cursor.isNull(2) || cursor.getInt(2) == 0 ? false : true);
            feedPostBuilder.setUserId(cursor.getString(3));
            String postTypeStr = cursor.getString(4);
            feedPostBuilder.setPostType(postTypeStr == null ? null : PostType.valueOf(postTypeStr));
            feedPostBuilder.setCategory(cursor.getString(5));
            feedPostBuilder.setMessage(cursor.getString(6));
            feedPostBuilder.setPictureUrl(cursor.getString(7));
            feedPostBuilder.setExternalLinkUrl(cursor.getString(8));
            feedPostBuilder.setExternalLinkImageUrl(cursor.getString(9));
            feedPostBuilder.setExternalLinkCaption(cursor.getString(10));
            feedPostBuilder.setExternalLinkSummary(cursor.getString(11));
            feedPostBuilder.setProductBarCode(cursor.getString(12));
            String publicityStr = cursor.getString(13);
            feedPostBuilder.setPublicity(
                    publicityStr == null ? null : PostPublicity.valueOf(publicityStr));
            feedPostBuilder.setLikes(cursor.isNull(14) ? 0 : cursor.getInt(14));
            feedPostBuilder.setComments(cursor.isNull(15) ? 0 : cursor.getInt(15));
            feedPostBuilder.setIsAnonymous(
                    cursor.isNull(16) || cursor.getInt(16) == 0 ? false : true);
            String statusStr = cursor.getString(17);
            feedPostBuilder.setPostStatus(statusStr == null ? null : PostStatus.valueOf(statusStr));
            feedPostBuilder.setTimeMsCreated(cursor.isNull(18) ? 0 : cursor.getLong(18));
            feedPostBuilder.setTimeMsEdited(cursor.isNull(19) ? 0 : cursor.getLong(19));
            feedPostBuilder.setTimeMsChangeToSurface(cursor.isNull(20) ? 0 : cursor.getLong(20));

            listBuilder.add(feedPostBuilder.build());
        }

        return listBuilder.build();
    }

    public static LoadFeedManager getInstance() {
        return INSTANCE;
    }

    /**
     * Load more feed post whose insert time is equal or less than 'timeSince'
     * @param timeSince the sort time of the last post in the feed list view
     */
    public void loadFeedMore(long timeSince) {
        loadFeed(new LoadDataParams(
                timeSince,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CHECK_SERVER,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }
}