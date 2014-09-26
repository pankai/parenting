package com.palmcel.parenting.comment;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataLoadCause;
import com.palmcel.parenting.common.DataSource;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.common.TriState;
import com.palmcel.parenting.feed.FeedHandler;
import com.palmcel.parenting.model.CommentsServiceFinishEvent;
import com.palmcel.parenting.model.CommentsServiceStartEvent;
import com.palmcel.parenting.model.LoadCommentsResultEvent;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.LoadPostDataParams;
import com.palmcel.parenting.model.PostComment;

import java.util.concurrent.Callable;

import de.greenrobot.event.EventBus;

/**
 * Singleton class that loads comments
 */
public class LoadCommentsManager {

    private static final String TAG = "LoadCommentsManager";
    private static final String LOAD_TAG = "Comments";
    private static final int DEFAULT_MAX_FETCH = 20;

    private static LoadCommentsManager INSTANCE = new LoadCommentsManager();

    private ListenableFuture<LoadDataResult<PostComment>> mLoadCommentsFuture;

    private LoadCommentsManager() {}

    public void loadComments(String postId) {
        loadComments(new LoadPostDataParams(
                postId,
                0,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CACHE_OK,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }

    public void loadComments(String postId, DataFreshnessParam dataFreshnessParam) {
        loadComments(new LoadPostDataParams(
                postId,
                0,
                DEFAULT_MAX_FETCH,
                dataFreshnessParam,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }

    public void loadCommentsAfterSubmit(String postId) {
        loadComments(new LoadPostDataParams(
                postId,
                0,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CHECK_SERVER,
                DataLoadCause.AFTER_SUBMIT,
                LOAD_TAG));
    }

    public void loadComments(final LoadPostDataParams loadPostDataParams) {
        if (mLoadCommentsFuture != null) {
            Log.d(TAG, "loadComments was skipped.");
            return;
        } else {
            Log.d(TAG, "In loadComments, loadDataParams=" + loadPostDataParams);
        }

        ImmutableList<PostComment> cachedComments =
                CommentsCache.getInstance().getEntitiesIfUpToDate(loadPostDataParams.postId);

        if (loadPostDataParams.dataFreshnessParam == DataFreshnessParam.CACHE_OK &&
                cachedComments != null) {
            LoadDataResult<PostComment> result = LoadDataResult.successResult(
                    cachedComments,
                    DataSource.MEMORY_CACHE);
            // Update comments listview with memory cache data
            EventBus.getDefault().post(
                    new LoadCommentsResultEvent(loadPostDataParams, result));
            Log.d(TAG, "To display comments from memory cache");
            return;
        }

        EventBus.getDefault().post(new CommentsServiceStartEvent());
        mLoadCommentsFuture = ExecutorUtil.execute(new Callable<LoadDataResult<PostComment>>() {
            @Override
            public LoadDataResult<PostComment> call() throws Exception {
                // Load from server
                FeedHandler feedHandler = new FeedHandler();
                final ImmutableList<PostComment> commentsFromServer =
                        feedHandler.getPostCommentsFromServer(
                                loadPostDataParams.postId,
                                loadPostDataParams.timeSince,
                                loadPostDataParams.maxToFetch
                        );

                ImmutableList<PostComment> recentCachedComments;
                if (loadPostDataParams.timeSince == 0) {
                    recentCachedComments =
                            CommentsCache.getInstance().updateEntitiesCacheFromServer(
                                loadPostDataParams.postId,
                                commentsFromServer);
                } else {
                    // Load-more case
                    recentCachedComments =
                            CommentsCache.getInstance().updateEntitiesCacheFromServer(
                                loadPostDataParams.postId,
                                loadPostDataParams.timeSince,
                                commentsFromServer);
                }


                TriState severHasMoreComments = TriState.FALSE;
                if (commentsFromServer.size() == loadPostDataParams.maxToFetch) {
                    severHasMoreComments = TriState.TRUE;
                }

                LoadDataResult<PostComment> result = LoadDataResult.successResult(
                        recentCachedComments,
                        DataSource.SERVER,
                        severHasMoreComments);

                Log.d(TAG, "To display comments from server");
                return result;
            }
        });

        Futures.addCallback(mLoadCommentsFuture, new FutureCallback<LoadDataResult<PostComment>>() {
            @Override
            public void onSuccess(LoadDataResult<PostComment> result) {
                Log.d(TAG, "mLoadCommentsFuture succeeded");
                mLoadCommentsFuture = null;
                EventBus.getDefault().post(new LoadCommentsResultEvent(loadPostDataParams, result));
                EventBus.getDefault().post(new CommentsServiceFinishEvent());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "mLoadCommentsFuture failed", t);
                mLoadCommentsFuture = null;
                LoadDataResult result = LoadDataResult.errorResult(t);
                EventBus.getDefault().post(new LoadCommentsResultEvent(loadPostDataParams, result));
                EventBus.getDefault().post(new CommentsServiceFinishEvent());
            }
        });
    }
    public static LoadCommentsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Load more post comments whose create time is equal or less than 'timeMsCreatedSince'
     * @param timeMsCreatedSince the create time of the last comments in the comments list view
     */
    public void loadCommentsMore(String postId, long timeMsCreatedSince) {
        loadComments(new LoadPostDataParams(
                postId,
                timeMsCreatedSince,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CHECK_SERVER,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }
}