package com.palmcel.parenting.model;

import com.google.common.base.MoreObjects;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataLoadCause;

/**
 * Client side parameters to load comments for a post
 */
public class LoadCommentsParams {

    // The post id for the comments to load
    public String postId;
    // Fetch comments in feed whose created time is older or equal to
    // 'timeMsCreatedSince'. If it is not 0, it is load more operation; otherwise it is
    // loading latest comments and just ignore it.
    public long timeMsCreatedSince;
    public int maxToFetch;
    public DataFreshnessParam dataFreshnessParam;
    public DataLoadCause dataLoadCause;

    public LoadCommentsParams(
            String postId,
            long timeMsCreatedSince,
            int maxToFetch,
            DataFreshnessParam dataFreshnessParam,
            DataLoadCause dataLoadCause) {
        this.postId = postId;
        this.timeMsCreatedSince = timeMsCreatedSince;
        this.maxToFetch = maxToFetch;
        this.dataFreshnessParam = dataFreshnessParam;
        this.dataLoadCause = dataLoadCause;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("postId", postId)
            .add("timeMsCreatedSince", timeMsCreatedSince)
            .add("maxToFetch", maxToFetch)
            .add("dataFreshnessParam", dataFreshnessParam)
            .add("dataLoadCause", dataLoadCause)
            .toString();
    }
}
