package com.palmcel.parenting.model;

/**
 * EventBus events for loading feed
 */
public class LoadFeedResultEvent {
    private LoadFeedParams mLoadFeedParams;
    private LoadFeedResult mLoadFeedResult;

    public LoadFeedResultEvent(LoadFeedParams params, LoadFeedResult result) {
        mLoadFeedParams = params;
        mLoadFeedResult = result;
    }

    public LoadFeedParams getLoadFeedParams() {
        return mLoadFeedParams;
    }

    public LoadFeedResult getLoadFeedResult() {
        return mLoadFeedResult;
    }
}
