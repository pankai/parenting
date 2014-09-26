package com.palmcel.parenting.model;

import com.google.common.base.MoreObjects;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataLoadCause;

/**
 * Client side parameters to load likes for a post
 */
public class LoadLikesParams {

    // The post id for the likes to load
    public String postId;
    // Fetch likes whose like time is older or equal to
    // 'timeMsLikeSince'. If it is not 0, it is load more operation; otherwise it is
    // loading latest likes and just ignore it.
    public long timeMsLikeSince;
    public int maxToFetch;
    public DataFreshnessParam dataFreshnessParam;
    public DataLoadCause dataLoadCause;

    public LoadLikesParams(
            String postId,
            long timeMsLikeSince,
            int maxToFetch,
            DataFreshnessParam dataFreshnessParam,
            DataLoadCause dataLoadCause) {
        this.postId = postId;
        this.timeMsLikeSince = timeMsLikeSince;
        this.maxToFetch = maxToFetch;
        this.dataFreshnessParam = dataFreshnessParam;
        this.dataLoadCause = dataLoadCause;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("postId", postId)
            .add("timeMsLikeSince", timeMsLikeSince)
            .add("maxToFetch", maxToFetch)
            .add("dataFreshnessParam", dataFreshnessParam)
            .add("dataLoadCause", dataLoadCause)
            .toString();
    }
}
