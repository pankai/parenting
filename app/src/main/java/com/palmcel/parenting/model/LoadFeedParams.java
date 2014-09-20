package com.palmcel.parenting.model;

import com.palmcel.parenting.common.DataFreshnessParam;

/**
 * Client side parameters to load feed
 */
public class LoadFeedParams {
    // Fetch post in feed whose insert time is older or equal to
    // 'priorEqualToTimestampMs'. Ignore it if it is 0.
    public long priorEqualToTimestampMs;
    public int maxToFetch;
    public DataFreshnessParam dataFreshnessParam;

    public LoadFeedParams(
            long priorEqualToTimestampMs,
            int maxToFetch,
            DataFreshnessParam dataFreshnessParam) {
        this.priorEqualToTimestampMs = priorEqualToTimestampMs;
        this.maxToFetch = maxToFetch;
        this.dataFreshnessParam = dataFreshnessParam;
    }
}
