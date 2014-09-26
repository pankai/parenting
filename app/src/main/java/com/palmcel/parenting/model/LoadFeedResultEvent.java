package com.palmcel.parenting.model;

/**
 * EventBus events for loading feed
 */
public class LoadFeedResultEvent {
    private LoadDataParams mLoadDataParams;
    private LoadDataResult<FeedPost> mLoadFeedResult;

    public LoadFeedResultEvent(LoadDataParams params, LoadDataResult<FeedPost> result) {
        mLoadDataParams = params;
        mLoadFeedResult = result;
    }

    public LoadDataParams getLoadDataParams() {
        return mLoadDataParams;
    }

    public LoadDataResult<FeedPost> getLoadFeedResult() {
        return mLoadFeedResult;
    }
}
