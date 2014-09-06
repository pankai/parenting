package com.palmcel.parenting.feed;

/**
 * Parameters to load feed
 */
public class LoadFeedParams {
    private long mSinceTimestampMs;
    private int mMaxToFetch;

    public LoadFeedParams(long sinceTimestampMs, int maxToFetch) {
        mSinceTimestampMs = sinceTimestampMs;
        mMaxToFetch = maxToFetch;
    }

    public long getSinceTimestampMs() {
        return mSinceTimestampMs;
    }

    public int getMaxToFetch() {
        return mMaxToFetch;
    }
}
