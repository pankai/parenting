package com.palmcel.parenting.likes;

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
import com.palmcel.parenting.model.LikesServiceFinishEvent;
import com.palmcel.parenting.model.LikesServiceStartEvent;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.LoadLikesResultEvent;
import com.palmcel.parenting.model.LoadPostDataParams;
import com.palmcel.parenting.model.PostLike;

import java.util.concurrent.Callable;

import de.greenrobot.event.EventBus;

/**
 * Singleton class that loads likes
 */
public class LoadLikesManager {

    private static final String TAG = "LoadLikesManager";
    private static final String LOAD_TAG = "Likes";
    private static final int DEFAULT_MAX_FETCH = 30;

    private static LoadLikesManager INSTANCE = new LoadLikesManager();

    private ListenableFuture<LoadDataResult<PostLike>> mLoadLikesFuture;

    private LoadLikesManager() {}

    public void loadLikes(String postId) {
        loadLikes(new LoadPostDataParams(
                postId,
                0,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CACHE_OK,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }

    public void loadLikes(String postId, DataFreshnessParam dataFreshnessParam) {
        loadLikes(new LoadPostDataParams(
                postId,
                0,
                DEFAULT_MAX_FETCH,
                dataFreshnessParam,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }

    public void loadLikes(final LoadPostDataParams loadPostDataParams) {
        if (mLoadLikesFuture != null) {
            Log.d(TAG, "loadLikes was skipped.");
            return;
        } else {
            Log.d(TAG, "In loadLikes, loadLikesParams=" + loadPostDataParams);
        }

        ImmutableList<PostLike> cachedLikes =
                LikesCache.getInstance().getEntitiesIfUpToDate(loadPostDataParams.postId);

        if (loadPostDataParams.dataFreshnessParam == DataFreshnessParam.CACHE_OK &&
                cachedLikes != null) {
            LoadDataResult<PostLike> result = LoadDataResult.successResult(
                    cachedLikes,
                    DataSource.MEMORY_CACHE);
            // Update likes listview with memory cache data
            EventBus.getDefault().post(
                    new LoadLikesResultEvent(loadPostDataParams, result));
            Log.d(TAG, "To display likes from memory cache");
            return;
        }

        EventBus.getDefault().post(new LikesServiceStartEvent());
        mLoadLikesFuture = ExecutorUtil.execute(new Callable<LoadDataResult<PostLike>>() {
            @Override
            public LoadDataResult<PostLike> call() throws Exception {
                // Load from server
                FeedHandler feedHandler = new FeedHandler();
                final ImmutableList<PostLike> likesFromServer =
                        feedHandler.getPostLikesFromServer(
                                loadPostDataParams.postId,
                                loadPostDataParams.timeSince,
                                loadPostDataParams.maxToFetch
                        );

                ImmutableList<PostLike> recentCachedLikes;
                if (loadPostDataParams.timeSince == 0) {
                    recentCachedLikes =
                            LikesCache.getInstance().updateEntitiesCacheFromServer(
                                    loadPostDataParams.postId,
                                    likesFromServer);
                } else {
                    // Load-more case
                    recentCachedLikes =
                            LikesCache.getInstance().updateEntitiesCacheFromServer(
                                    loadPostDataParams.postId,
                                    loadPostDataParams.timeSince,
                                    likesFromServer);
                }


                TriState severHasMoreLikes = TriState.FALSE;
                if (likesFromServer.size() == loadPostDataParams.maxToFetch) {
                    severHasMoreLikes = TriState.TRUE;
                }

                LoadDataResult<PostLike> result = LoadDataResult.successResult(
                        recentCachedLikes,
                        DataSource.SERVER,
                        severHasMoreLikes);

                Log.d(TAG, "To display likes from server");
                return result;
            }
        });

        Futures.addCallback(mLoadLikesFuture, new FutureCallback<LoadDataResult<PostLike>>() {
            @Override
            public void onSuccess(LoadDataResult<PostLike> result) {
                Log.d(TAG, "mLoadLikesFuture succeeded");
                mLoadLikesFuture = null;
                EventBus.getDefault().post(new LoadLikesResultEvent(loadPostDataParams, result));
                EventBus.getDefault().post(new LikesServiceFinishEvent());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "mLoadLikesFuture failed", t);
                mLoadLikesFuture = null;
                LoadDataResult result = LoadDataResult.errorResult(t);
                EventBus.getDefault().post(new LoadLikesResultEvent(loadPostDataParams, result));
                EventBus.getDefault().post(new LikesServiceFinishEvent());
            }
        });
    }
    public static LoadLikesManager getInstance() {
        return INSTANCE;
    }

    /**
     * Load more post likes whose like time is equal or less than 'timeMsLikeSince'
     * @param timeMsLikeSince the like time of the last like in the likes list view
     */
    public void loadLikesMore(String postId, long timeMsLikeSince) {
        loadLikes(new LoadPostDataParams(
                postId,
                timeMsLikeSince,
                DEFAULT_MAX_FETCH,
                DataFreshnessParam.CHECK_SERVER,
                DataLoadCause.UNKNOWN,
                LOAD_TAG));
    }
}