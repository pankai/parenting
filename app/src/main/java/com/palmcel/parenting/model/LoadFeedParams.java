package com.palmcel.parenting.model;

import com.palmcel.parenting.common.DataFreshnessParam;

/**
 * Parameters to load feed
 */
public class LoadFeedParams {
    public long sinceTimestampMs;
    public int maxToFetch;
    public DataFreshnessParam dataFreshnessParam;

    public LoadFeedParams(
            long sinceTimestampMs,
            int maxToFetch,
            DataFreshnessParam dataFreshnessParam) {
        this.sinceTimestampMs = sinceTimestampMs;
        this.maxToFetch = maxToFetch;
        this.dataFreshnessParam = dataFreshnessParam;
    }
}
