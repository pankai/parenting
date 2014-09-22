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

    public synchronized ImmutableList<FeedPost> getCachedFeed() {
        return mCachedFeed;
    }

    public synchronized boolean isEmpty() {
        return mCachedFeed.isEmpty();
    }

    /**
     * Update memory cache with data from database.
     * Merge mCachedFeed and dbFeedPost into mCacheFeed.
     * There should be no hole in the merge results.
     * @param dbFeedPosts data from db
     */
    public synchronized void updateCacheFromDb(ImmutableList<FeedPost> dbFeedPosts) {
        Log.d(TAG, "In updateCacheFromDb, mCachedFeed=" + mCachedFeed.size() +
                ", dbFeedPosts=" + dbFeedPosts.size());
        if (mCachedFeed.isEmpty()) {
            mCachedFeed = dbFeedPosts;
            mLastUpdatedMs = System.currentTimeMillis(); // TODO: should get the time from dbFeedPosts
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
    public synchronized long getLargestInsertTime() {
        if (mCachedFeed.isEmpty()) {
            return 0;
        }

        return mCachedFeed.get(0).timeMsInserted;
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
     * Update mCachedFeed with the latest feed loaded from server
     * @param feedFromServer feed from server. The feed starts from the latest post in the feed. It
     *                       is not middle portion in the feed.
     */
    public synchronized void updateCacheFromServer(ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateCacheFromServer, mCachedFeed=" + mCachedFeed.size() +
                ", feedFromServer=" + feedFromServer.size());
        if (mCachedFeed.isEmpty()) {
            mCachedFeed = feedFromServer;
            mLastUpdatedMs = System.currentTimeMillis();
            return;
        }
        if (feedFromServer.isEmpty()) {
            Log.d(TAG, "updateCacheFromServer, empty feed from server");
            return;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(feedFromServer);

        FeedPost lastInServerFeed = feedFromServer.get(feedFromServer.size() - 1);

        boolean hasFoundLast = false;

        for (FeedPost post: mCachedFeed) {
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

    /**
     * Update mCachedFeed with the load-more feed from server
     * @param timeMsInsertedSince, the smallest insert time of the feed at client before
     *                             loading more
     * @param feedFromServer feed from server. The feed is a result of load more. That is, it
     *                        doesn't start with the latest post in the feed at server.
     */
    public synchronized void updateCacheFromServer(
            long timeMsInsertedSince,
            ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateCacheFromServer for load-more, mCachedFeed=" + mCachedFeed.size() +
                ", feedFromServer=" + feedFromServer.size() +
                ", timeMsInsertedSince=" + timeMsInsertedSince);
        if (mCachedFeed.isEmpty()) {
            mCachedFeed = feedFromServer;
            Log.e(TAG, "mCachedFeed became empty after loading more", new RuntimeException());
            return;
        }
        if (feedFromServer.isEmpty()) {
            Log.d(TAG, "updateCacheFromServer for load-more, empty feed from server");
            return;
        }

        FeedPost lastInMemory = mCachedFeed.get(mCachedFeed.size() - 1);
        FeedPost firstFromServer = feedFromServer.get(0);

        if (timeMsInsertedSince != firstFromServer.timeMsInserted) {
            Log.e(TAG, "Inconsistent timeMsInsertedSince after loading more, " +
                    timeMsInsertedSince + " vs " +
                    firstFromServer.timeMsInserted, new RuntimeException());
            return;
        }

        if (lastInMemory.timeMsInserted != firstFromServer.timeMsInserted) {
            Log.e(TAG, "Unmatched timeMsInsertedSince after loading more, " +
                    lastInMemory.timeMsInserted + " vs " +
                    firstFromServer.timeMsInserted, new RuntimeException());
            return;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(mCachedFeed.subList(0, mCachedFeed.size() - 1));
        builder.addAll(feedFromServer);

        mCachedFeed = builder.build();
    }
}
