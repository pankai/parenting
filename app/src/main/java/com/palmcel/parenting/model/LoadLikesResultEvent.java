package com.palmcel.parenting.model;

/**
 * EventBus events for loading post likes
 */
public class LoadLikesResultEvent {
    private LoadDataParams mLoadDataParams;
    private LoadDataResult<PostLike> mLoadLikesResult;

    public LoadLikesResultEvent(LoadDataParams params, LoadDataResult<PostLike> result) {
        mLoadDataParams = params;
        mLoadLikesResult = result;
    }

    public LoadDataParams getLoadPostDataParams() {
        return mLoadDataParams;
    }

    public LoadDataResult<PostLike> getLoadLikesResult() {
        return mLoadLikesResult;
    }
}
