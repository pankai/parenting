package com.palmcel.parenting.feed;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataSource;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
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

    public void loadFeed(final LoadFeedParams loadFeedParams) {
        if (mLoadFeedFuture != null) {
            Log.d(TAG, "loadFeed was skipped.");
            return;
        } else {
            Log.d(TAG, "In loadFeed");
        }

        if (loadFeedParams.dataFreshnessParam == DataFreshnessParam.CACHE_OK &&
                FeedCache.getInstance().isUpToDate() &&
                !FeedCache.getInstance().isEmpty()) {
            LoadFeedResult result = LoadFeedResult.successResult(
                    FeedCache.getInstance().getCachedFeed(),
                    DataSource.MEMORY_CACHE);
            // Update feed listview with memory cache data
            EventBus.getDefault().post(
                    new LoadFeedResultEvent(loadFeedParams, result));
            Log.d(TAG, "Loaded feed from memory cache");
            return;
        }

        mLoadFeedFuture = ExecutorUtil.execute(new Callable<LoadFeedResult>() {
            @Override
            public LoadFeedResult call() throws Exception {
                LoadFeedResult dbResult = loadFeedFromDb(loadFeedParams);
                // Check db cache is less stale than memory cache
                FeedCache.getInstance().updateCache(dbResult.feedPosts);
                if (loadFeedParams.dataFreshnessParam == DataFreshnessParam.CACHE_OK &&
                        !dbResult.isEmpty() && false) {
                    // TODO: check stale of db data
                    // Update feed listview with db data
                    Log.d(TAG, "Loaded feed from database");
                    return dbResult;
                } else {
                    // Load from server
                    FeedHandler feedHandler = new FeedHandler();
                    ImmutableList<FeedPost> feedFromServer = feedHandler.getFeedPostFromServer(
                            "pkdebug", // TODO
                            loadFeedParams.maxToFetch,
                            FeedCache.getInstance().getLargestInsertTime()
                    );
                    Log.d(TAG, "Loaded feed from server");
                    return LoadFeedResult.successResult(feedFromServer, DataSource.SERVER);
                }
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

        String selectFields =
            PostEntry.COLUMN_POST_ID + ", " +
            FeedEntry.COLUMN_TIME_INSERTED + ", " +
            PostEntry.COLUMN_USER_ID + ", " +
            PostEntry.COLUMN_POST_TYPE + ", " +
            PostEntry.COLUMN_POST_CATEGORY + ", " +
            PostEntry.COLUMN_MESSAGE + ", " +
            PostEntry.COLUMN_PICTURE_URL + ", " +
            PostEntry.COLUMN_EXT_LINK_URL + ", " +
            PostEntry.COLUMN_EXT_LINK_IMAGE_URL + ", " +
            PostEntry.COLUMN_EXT_LINK_CAPTION + ", " +
            PostEntry.COLUMN_EXT_LINK_SUMMARY + ", " +
            PostEntry.COLUMN_PRODUCT_BAR_CODE + ", " +
            PostEntry.COLUMN_PUBLICITY + ", " +
            PostEntry.COLUMN_LIKES + ", " +
            PostEntry.COLUMN_COMMENTS + ", " +
            PostEntry.COLUMN_IS_ANONYMOUS + ", " +
            PostEntry.COLUMN_STATUS + ", " +
            PostEntry.COLUMN_TIME_CREATED + ", " +
            PostEntry.COLUMN_TIME_EDITED + ", " +
            PostEntry.COLUMN_TIME_CHANGE_TO_SURFACE;

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

    public static LoadFeedManager getInstance() {
        return INSTANCE;
    }
}