package com.palmcel.parenting.model;

/**
 * EventBus events for loading post comments
 */
public class LoadCommentsResultEvent {
    private LoadDataParams mLoadDataParams;
    private LoadDataResult<PostComment> mLoadCommentsResult;

    public LoadCommentsResultEvent(LoadDataParams params, LoadDataResult<PostComment> result) {
        mLoadDataParams = params;
        mLoadCommentsResult = result;
    }

    public LoadDataParams getLoadPostDataParams() {
        return mLoadDataParams;
    }

    public LoadDataResult<PostComment> getLoadCommentsResult() {
        return mLoadCommentsResult;
    }
}
