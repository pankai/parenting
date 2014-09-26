package com.palmcel.parenting.model;

/**
 * EventBus events for loading post comments
 */
public class LoadCommentsResultEvent {
    private LoadPostDataParams mLoadPostDataParams;
    private LoadDataResult<PostComment> mLoadCommentsResult;

    public LoadCommentsResultEvent(LoadPostDataParams params, LoadDataResult<PostComment> result) {
        mLoadPostDataParams = params;
        mLoadCommentsResult = result;
    }

    public LoadPostDataParams getLoadPostDataParams() {
        return mLoadPostDataParams;
    }

    public LoadDataResult<PostComment> getLoadCommentsResult() {
        return mLoadCommentsResult;
    }
}
