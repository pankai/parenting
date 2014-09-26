package com.palmcel.parenting.model;

/**
 * EventBus events for loading post likes
 */
public class LoadLikesResultEvent {
    private LoadLikesParams mLoadLikesParams;
    private LoadDataResult<PostLike> mLoadLikesResult;

    public LoadLikesResultEvent(LoadLikesParams params, LoadDataResult<PostLike> result) {
        mLoadLikesParams = params;
        mLoadLikesResult = result;
    }

    public LoadLikesParams getLoadLikesParams() {
        return mLoadLikesParams;
    }

    public LoadDataResult<PostLike> getLoadLikesResult() {
        return mLoadLikesResult;
    }
}
