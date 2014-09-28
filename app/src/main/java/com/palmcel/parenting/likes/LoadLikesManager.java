package com.palmcel.parenting.likes;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.feed.FeedHandler;
import com.palmcel.parenting.loadmanager.LoadDataManager;
import com.palmcel.parenting.model.LikesServiceFinishEvent;
import com.palmcel.parenting.model.LikesServiceStartEvent;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.LoadLikesResultEvent;
import com.palmcel.parenting.model.LoadDataParams;
import com.palmcel.parenting.model.PostLike;

import de.greenrobot.event.EventBus;

/**
 * Singleton class that loads likes
 */
public class LoadLikesManager extends LoadDataManager<PostLike> {

    private static final String TAG = "LoadLikesManager";
    private static final String LOAD_TAG = "Likes";
    private static final int DEFAULT_MAX_FETCH = 30;

    private static LoadLikesManager INSTANCE = new LoadLikesManager();

    private ListenableFuture<LoadDataResult<PostLike>> mLoadLikesFuture;

    private LoadLikesManager() {
        super(LOAD_TAG, DEFAULT_MAX_FETCH, LikesCache.getInstance());
    }

    public static LoadLikesManager getInstance() {
        return INSTANCE;
    }

    @Override
    protected void notifyDataLoadResult(
            LoadDataParams loadDataParams,
            LoadDataResult<PostLike> result) {
        EventBus.getDefault().post(
                new LoadLikesResultEvent(loadDataParams, result));
    }

    @Override
    protected void notifyServiceStart() {
        EventBus.getDefault().post(new LikesServiceStartEvent());
    }

    @Override
    protected void notifyServiceFinish() {
        EventBus.getDefault().post(new LikesServiceFinishEvent());
    }

    @Override
    protected ImmutableList<PostLike> loadDataFromServer(
            String postId,
            long timeSince,
            int maxToFetch) {
        // Load from server
        FeedHandler feedHandler = new FeedHandler();
        return feedHandler.getPostLikesFromServer(postId, timeSince, maxToFetch);
    }
}