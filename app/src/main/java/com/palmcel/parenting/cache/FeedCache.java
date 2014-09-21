package com.palmcel.parenting.cache;

import android.database.sqlite.SQLiteException;
import android.text.format.DateUtils;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.db.DatabaseContract;
import com.palmcel.parenting.db.DbHelper;
import com.palmcel.parenting.model.FeedPost;

/**
 * Singleton that caches the feed
 */
public class FeedCache {

    private static final String TAG = "FeedCache";
    private static final FeedCache INSTANCE = new FeedCache();
    private static final long sStaleIntervalMs = 3 * DateUtils.MINUTE_IN_MILLIS;

    private ImmutableList<FeedPost> mCachedFeed;
    private long mLastUpdatedMs;

    private FeedCache() {
        mCachedFeed = ImmutableList.of();
    }

    public static FeedCache getInstance() {
        return INSTANCE;
    }

    public boolean isUpToDate() {
        return System.currentTimeMillis() - mLastUpdatedMs < sStaleIntervalMs;
    }

    public ImmutableList<FeedPost> getCachedFeed() {
        return mCachedFeed;
    }

    public boolean isEmpty() {
        return mCachedFeed.isEmpty();
    }

    /**
     * Update memory cache with data from database.
     * Merge mCachedFeed and dbFeedPost into mCacheFeed.
     * There should be no hole in the merge results.
     * @param dbFeedPosts data from db
     */
    public void updateCacheFromDb(ImmutableList<FeedPost> dbFeedPosts) {
        Log.d(TAG, "In updateCacheFromDb, mCachedFeed=" + mCachedFeed.size() +
                ", dbFeedPosts=" + dbFeedPosts.size());
        if (mCachedFeed.isEmpty()) {
            mCachedFeed = dbFeedPosts;
            mLastUpdatedMs = System.currentTimeMillis();
            return;
        }
        if (dbFeedPosts.isEmpty()) {
            return;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(mCachedFeed);

        FeedPost lastInMemCache = mCachedFeed.get(mCachedFeed.size() - 1);

        boolean hasFoundLast = false;

        for (FeedPost post: dbFeedPosts) {
            if (hasFoundLast) {
                builder.add(post);
            } else if (post.timeMsInserted == lastInMemCache.timeMsInserted) {
                hasFoundLast = true;
            } else if (post.timeMsInserted < lastInMemCache.timeMsInserted) {
                // There is hole between memory cache and dbFeedPosts.
                Log.e(TAG, "Error, there is a hole between memory cache and dbFeedPosts.");
                clearFeedPostTableOnThread();
                return;
            }
        }

        mLastUpdatedMs = System.currentTimeMillis();
        mCachedFeed = builder.build();
    }

    /**
     * @return the largest insert time of post in mCachedFeed or 0 if mCachedFeed is empty.
     */
    public long getLargestInsertTime() {
        ImmutableList<FeedPost> cachedFeed = ImmutableList.copyOf(mCachedFeed);
        if (cachedFeed.isEmpty()) {
            return 0;
        }

        return cachedFeed.get(0).timeMsInserted;
    }

    /**
     * Clear all rows in feed_post table on a worker thread
     */
    private void clearFeedPostTableOnThread() {
        Log.d(TAG, "In clearFeedPostTableOnThread");
        ExecutorUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DbHelper.getDb().delete(DatabaseContract.FeedEntry.TABLE_NAME, null, null);
                } catch (SQLiteException ex) {
                    Log.e(TAG, "clearFeedPostTable failed.", ex);
                }
            }
        });
    }
}
