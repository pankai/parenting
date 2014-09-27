package com.palmcel.parenting.model;

import com.google.common.base.MoreObjects;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataLoadCause;

/**
 * Client side parameters to load data, e.g. feed, comments, likes, etc
 */
public class LoadDataParams {

    // Post id. It is not used for loading feed.
    public String postId;
    // Fetch data whose time (create time, insert time, etc) is older or equal to
    // 'timeSince'. If it is not 0, it is load more operation; otherwise it is
    // loading latest data and just ignore it.
    public long timeSince;
    public int maxToFetch;
    public DataFreshnessParam dataFreshnessParam;
    public DataLoadCause dataLoadCause;
    public String tag;

    public LoadDataParams(
            String postId,
            long timeSince,
            int maxToFetch,
            DataFreshnessParam dataFreshnessParam,
            DataLoadCause dataLoadCause,
            String tag) {
        this.postId = postId;
        this.timeSince = timeSince;
        this.maxToFetch = maxToFetch;
        this.dataFreshnessParam = dataFreshnessParam;
        this.dataLoadCause = dataLoadCause;
        this.tag = tag;
    }

    public LoadDataParams(
            long timeSince,
            int maxToFetch,
            DataFreshnessParam dataFreshnessParam,
            DataLoadCause dataLoadCause,
            String tag) {
        this.timeSince = timeSince;
        this.maxToFetch = maxToFetch;
        this.dataFreshnessParam = dataFreshnessParam;
        this.dataLoadCause = dataLoadCause;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("tag", tag)
            .add("postId", postId)
            .add("timeSince", timeSince)
            .add("maxToFetch", maxToFetch)
            .add("dataFreshnessParam", dataFreshnessParam)
            .add("dataLoadCause", dataLoadCause)
            .toString();
    }
}
