package com.palmcel.parenting.feed;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.db.DbHelper;
import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.FeedPostBuilder;
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
        loadFeed(new LoadFeedParams(0, DEFAULT_MAX_FETCH));
    }

    public void loadFeed(final LoadFeedParams loadFeedParams) {
        if (mLoadFeedFuture != null) {
            Log.d(TAG, "loadFeed was skipped.");
            return;
        } else {
            Log.d(TAG, "In loadFeed");
        }

        mLoadFeedFuture = ExecutorUtil.execute(new Callable<LoadFeedResult>() {
            @Override
            public LoadFeedResult call() throws Exception {
                return loadFeedFromDb(loadFeedParams);
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
            FeedEntry.COLUMN_FEED_ID + ", " +
            FeedEntry.TABLE_NAME + "." + FeedEntry.COLUMN_POST_ID + ", " +
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
            PostEntry.COLUMN_TIME_COMMENTED + ", " +
            PostEntry.COLUMN_TIME_LASTUPDATED;

        String query =
            "SELECT " + selectFields + " " +
            "FROM " + FeedEntry.TABLE_NAME + " INNER JOIN " + PostEntry.TABLE_NAME + " " +
            "  ON " + FeedEntry.TABLE_NAME + "." + FeedEntry.COLUMN_POST_ID +
                  " = " + PostEntry.TABLE_NAME + "." + PostEntry.COLUMN_POST_ID + " " +
            "ORDER BY " + FeedEntry.COLUMN_TIME_INSERTED + " DESC " +
            "LIMIT " + loadFeedParams.getMaxToFetch();

        Cursor cursor = db.rawQuery(query, null);

        ImmutableList.Builder<FeedPost> listBuilder = ImmutableList.builder();

        while (cursor.moveToNext()) {
            FeedPostBuilder feedPostBuilder = new FeedPostBuilder();

            feedPostBuilder.setFeedId(cursor.getString(0));
            feedPostBuilder.setPostId(cursor.getString(1));
            feedPostBuilder.setTimeMsInserted(cursor.isNull(2) ? 0 : cursor.getLong(2));
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
            feedPostBuilder.setTimeMsCommented(cursor.isNull(20) ? 0 : cursor.getLong(20));
            feedPostBuilder.setTimeMsLastUpdated(cursor.isNull(21) ? 0 : cursor.getLong(21));

            listBuilder.add(feedPostBuilder.build());
        }

        return LoadFeedResult.successResult(listBuilder.build());
    }

    public static LoadFeedManager getInstance() {
        return INSTANCE;
    }
}