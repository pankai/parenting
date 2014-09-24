package com.palmcel.parenting.model;

/**
 * EventBus events for loading post comments
 */
public class LoadCommentsResultEvent {
    private LoadCommentsParams mLoadCommentsParams;
    private LoadDataResult<PostComment> mLoadCommentsResult;

    public LoadCommentsResultEvent(LoadCommentsParams params, LoadDataResult<PostComment> result) {
        mLoadCommentsParams = params;
        mLoadCommentsResult = result;
    }

    public LoadCommentsParams getLoadCommentsParams() {
        return mLoadCommentsParams;
    }

    public LoadDataResult<PostComment> getLoadCommentsResult() {
        return mLoadCommentsResult;
    }
}
