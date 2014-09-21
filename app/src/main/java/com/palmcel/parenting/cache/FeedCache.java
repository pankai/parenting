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
        ImmutableList<FeedPost> cachedFeed = ImmutableList.copyOf(mCachedFeed);
        if (cachedFeed.isEmpty()) {
            mCachedFeed = dbFeedPosts;
            mLastUpdatedMs = System.currentTimeMillis(); // TODO: should get the time from dbFeedPosts
            return;
        }
        if (dbFeedPosts.isEmpty()) {
            return;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(cachedFeed);

        FeedPost lastInMemCache = cachedFeed.get(cachedFeed.size() - 1);

        boolean hasFoundLast = false;

        for (FeedPost post: dbFeedPosts) {
            if (hasFoundLast) {
                builder.add(post);
            } else if (post.timeMsInserted == lastInMemCache.timeMsInserted) {
                hasFoundLast = true;
            } else if (post.timeMsInserted < lastInMemCache.timeMsInserted) {
                // There is hole between memory cache and dbFeedPosts.
                Log.w(TAG, "Warning, there is a hole between memory cache and dbFeedPosts.");
                clearFeedPostTableOnThread();
                return;
            }
        }

        mLastUpdatedMs = System.currentTimeMillis();  // TODO: should get the time from dbFeedPosts
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

    /**
     * Update mCachedFeed with feed loaded from server
     * @param feedFromServer feed from server. The feed starts from the latest post in the feed. It
     *                       is not middle portion in the feed.
     */
    public void updateCacheFromServer(ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateCacheFromServer, mCachedFeed=" + mCachedFeed.size() +
                ", feedFromServer=" + feedFromServer.size());
        ImmutableList<FeedPost> cachedFeed = ImmutableList.copyOf(mCachedFeed);
        if (cachedFeed.isEmpty()) {
            mCachedFeed = feedFromServer;
            mLastUpdatedMs = System.currentTimeMillis();
            return;
        }
        if (feedFromServer.isEmpty()) {
            return;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(feedFromServer);

        FeedPost lastInServerFeed = feedFromServer.get(feedFromServer.size() - 1);

        boolean hasFoundLast = false;

        for (FeedPost post: cachedFeed) {
            if (hasFoundLast) {
                builder.add(post);
            } else if (post.timeMsInserted == lastInServerFeed.timeMsInserted) {
                hasFoundLast = true;
            } else if (post.timeMsInserted < lastInServerFeed.timeMsInserted) {
                // There is hole between memory cache and feedFromServer.
                Log.w(TAG, "Warning, there is a hole between memory cache and feedFromServer.");
                mLastUpdatedMs = System.currentTimeMillis();
                mCachedFeed = feedFromServer;
                return;
            }
        }

        mLastUpdatedMs = System.currentTimeMillis();
        mCachedFeed = builder.build();
    }
}
