package com.palmcel.parenting.model;

/**
 * Parameters to load feed
 */
public class LoadFeedParams {
    public long sinceTimestampMs;
    public int maxToFetch;

    public LoadFeedParams(long sinceTimestampMs, int maxToFetch) {
        this.sinceTimestampMs = sinceTimestampMs;
        this.maxToFetch = maxToFetch;
    }
}
