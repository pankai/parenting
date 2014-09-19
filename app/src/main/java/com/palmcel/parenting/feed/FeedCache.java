package com.palmcel.parenting.feed;

import android.text.format.DateUtils;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.Log;
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
     * Update memory cache with data from database or server.
     * Merge mCachedFeed and feedPost into mCacheFeed. There should be no hole in the merge results.
     * @param feedPosts data from db or server
     */
    public void updateCache(ImmutableList<FeedPost> feedPosts) {
        Log.d(TAG, "In updateCache, mCachedFeed=" + mCachedFeed.size() +
                ", feedPosts=" + feedPosts.size());
        if (mCachedFeed.isEmpty()) {
            mCachedFeed = feedPosts;
            mLastUpdatedMs = System.currentTimeMillis();
            return;
        }
        if (feedPosts.isEmpty()) {
            return;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(mCachedFeed);

        FeedPost lastInMemCache = mCachedFeed.get(mCachedFeed.size() - 1);

        boolean hasFoundLast = false;

        for (FeedPost post: feedPosts) {
            if (hasFoundLast) {
                builder.add(post);
            } else if (post.timeMsInserted == lastInMemCache.timeMsInserted) {
                hasFoundLast = true;
            } else if (post.timeMsInserted < lastInMemCache.timeMsInserted) {
                // There is hole between memory cache and feedPosts.
                Log.e(TAG, "Error, there is a hole between memory cache and feedPosts.");
                return;
            }
        }

        mLastUpdatedMs = System.currentTimeMillis();
        mCachedFeed = builder.build();
    }
}
