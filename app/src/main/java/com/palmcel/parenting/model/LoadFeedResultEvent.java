package com.palmcel.parenting.model;

/**
 * EventBus events for loading feed
 */
public class LoadFeedResultEvent {
    private LoadFeedParams mLoadFeedParams;
    private LoadDataResult<FeedPost> mLoadFeedResult;

    public LoadFeedResultEvent(LoadFeedParams params, LoadDataResult<FeedPost> result) {
        mLoadFeedParams = params;
        mLoadFeedResult = result;
    }

    public LoadFeedParams getLoadFeedParams() {
        return mLoadFeedParams;
    }

    public LoadDataResult<FeedPost> getLoadFeedResult() {
        return mLoadFeedResult;
    }
}
