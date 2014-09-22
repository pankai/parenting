package com.palmcel.parenting.model;

import com.google.common.base.MoreObjects;
import com.palmcel.parenting.common.DataLoadCause;
import com.palmcel.parenting.common.DataFreshnessParam;

/**
 * Client side parameters to load feed
 */
public class LoadFeedParams {
    // Fetch post in feed whose insert time is older or equal to
    // 'timeMsInsertedSince'. If it is not 0, it is load more operation; otherwise it is
    // loading latest feed and just ignore it.
    public long timeMsInsertedSince;
    public int maxToFetch;
    public DataFreshnessParam dataFreshnessParam;
    public DataLoadCause dataLoadCause;

    public LoadFeedParams(
            long timeMsInsertedSince,
            int maxToFetch,
            DataFreshnessParam dataFreshnessParam,
            DataLoadCause dataLoadCause) {
        this.timeMsInsertedSince = timeMsInsertedSince;
        this.maxToFetch = maxToFetch;
        this.dataFreshnessParam = dataFreshnessParam;
        this.dataLoadCause = dataLoadCause;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("timeMsInsertedSince", timeMsInsertedSince)
            .add("maxToFetch", maxToFetch)
            .add("dataFreshnessParam", dataFreshnessParam)
            .add("dataLoadCause", dataLoadCause)
            .toString();
    }
}
