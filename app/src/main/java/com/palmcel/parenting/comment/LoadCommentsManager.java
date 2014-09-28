package com.palmcel.parenting.comment;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataLoadCause;
import com.palmcel.parenting.feed.FeedHandler;
import com.palmcel.parenting.loadmanager.LoadDataManager;
import com.palmcel.parenting.model.CommentsServiceFinishEvent;
import com.palmcel.parenting.model.CommentsServiceStartEvent;
import com.palmcel.parenting.model.LoadCommentsResultEvent;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.LoadDataParams;
import com.palmcel.parenting.model.PostComment;

import de.greenrobot.event.EventBus;

/**
 * Singleton class that loads comments
 */
public class LoadCommentsManager extends LoadDataManager<PostComment> {

    private static final String TAG = "LoadCommentsManager";
    private static final String LOAD_TAG = "Comments";
    private static final int DEFAULT_MAX_FETCH = 20;

    private static LoadCommentsManager INSTANCE = new LoadCommentsManager();

    private LoadCommentsManager() {
        super(LOAD_TAG, DEFAULT_MAX_FETCH, CommentsCache.getInstance());
    }

    public static LoadCommentsManager getInstance() {
        return INSTANCE;
    }

    public void loadCommentsAfterSubmit(String postId) {
        loadData(new LoadDataParams(
                postId,
                0,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CHECK_SERVER,
                DataLoadCause.AFTER_SUBMIT,
                LOAD_TAG));
    }

    // Notify load-comments result to UI for updating UI
    @Override
    protected void notifyDataLoadResult(
            LoadDataParams loadDataParams,
            LoadDataResult<PostComment> result) {
        EventBus.getDefault().post(
                new LoadCommentsResultEvent(loadDataParams, result));
    }

    @Override
    protected void notifyServiceStart() {
        EventBus.getDefault().post(new CommentsServiceStartEvent());
    }

    @Override
    protected void notifyServiceFinish() {
        EventBus.getDefault().post(new CommentsServiceFinishEvent());
    }

    @Override
    protected ImmutableList<PostComment> loadDataFromServer(
            String postId,
            long timeSince,
            int maxToFetch) {
        // Load from server
        FeedHandler feedHandler = new FeedHandler();
        return feedHandler.getPostCommentsFromServer(postId, timeSince, maxToFetch);
    }
}