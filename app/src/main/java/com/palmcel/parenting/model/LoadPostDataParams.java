package com.palmcel.parenting.model;

import com.google.common.base.MoreObjects;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataLoadCause;

/**
 * Client side parameters to load data, e.g. comments, likes, etc for a post
 */
public class LoadPostDataParams extends LoadDataParams {

    public String postId;

    public LoadPostDataParams(
            String postId,
            long timeSince,
            int maxToFetch,
            DataFreshnessParam dataFreshnessParam,
            DataLoadCause dataLoadCause,
            String tag) {
        super(timeSince, maxToFetch, dataFreshnessParam, dataLoadCause, tag);
        this.postId = postId;
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
