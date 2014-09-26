package com.palmcel.parenting.model;

/**
 * EventBus events for loading post likes
 */
public class LoadLikesResultEvent {
    private LoadPostDataParams mLoadPostDataParams;
    private LoadDataResult<PostLike> mLoadLikesResult;

    public LoadLikesResultEvent(LoadPostDataParams params, LoadDataResult<PostLike> result) {
        mLoadPostDataParams = params;
        mLoadLikesResult = result;
    }

    public LoadPostDataParams getLoadPostDataParams() {
        return mLoadPostDataParams;
    }

    public LoadDataResult<PostLike> getLoadLikesResult() {
        return mLoadLikesResult;
    }
}
