package com.palmcel.parenting.loadmanager;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.cache.EntityCache;
import com.palmcel.parenting.common.DataFreshnessParam;
import com.palmcel.parenting.common.DataLoadCause;
import com.palmcel.parenting.common.DataSource;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.common.TriState;
import com.palmcel.parenting.model.LoadDataParams;
import com.palmcel.parenting.model.LoadDataResult;
import com.palmcel.parenting.model.SortByTimeEntity;

import java.util.concurrent.Callable;

/**
 * Generic load manager for comments and likes, which only have memory cache, not db cache.
 */
public abstract class LoadDataManager<T extends SortByTimeEntity> {

    private static final String TAG = "LoadDataManager";

    private final String mLoadTag;
    private final int mMaxFetch;
    private final EntityCache<T> mMemCache;

    private ListenableFuture<LoadDataResult<T>> mLoadDataFuture;

    public LoadDataManager(String loadTag, int maxFetch, EntityCache<T> memCache) {
        mLoadTag = loadTag;
        mMaxFetch = maxFetch;
        mMemCache = memCache;
    }

    public void loadData(String postId) {
        loadData(new LoadDataParams(
                postId,
                0,
                mMaxFetch,
                DataFreshnessParam.CACHE_OK,
                DataLoadCause.UNKNOWN,
                mLoadTag));
    }

    public void loadData(String postId, DataFreshnessParam dataFreshnessParam) {
        loadData(new LoadDataParams(
                postId,
                0,
                mMaxFetch,
                dataFreshnessParam,
                DataLoadCause.UNKNOWN,
                mLoadTag));
    }

    public void loadData(final LoadDataParams loadDataParams) {
        if (mLoadDataFuture != null) {
            Log.d(TAG, "loadData was skipped for " + mLoadTag);
            return;
        } else {
            Log.d(TAG, "In loadData for " + mLoadTag + ", loadDataParams=" + loadDataParams);
        }

        ImmutableList<T> cachedData =
                mMemCache.getEntitiesIfUpToDate(loadDataParams.postId);

        if (loadDataParams.dataFreshnessParam == DataFreshnessParam.CACHE_OK &&
                cachedData != null) {
            LoadDataResult<T> result = LoadDataResult.successResult(
                    cachedData,
                    DataSource.MEMORY_CACHE);
            // Notify the UI that loaded data
            notifyDataLoadResult(loadDataParams, result);
            Log.d(TAG, "To display data from memory cache for " + mLoadTag);
            return;
        }

        notifyServiceStart();
        mLoadDataFuture = ExecutorUtil.execute(new Callable<LoadDataResult<T>>() {
            @Override
            public LoadDataResult<T> call() throws Exception {
                // Load from server
                final ImmutableList<T> dataFromServer = loadDataFromServer(
                        loadDataParams.postId,
                        loadDataParams.timeSince,
                        loadDataParams.maxToFetch);
                ImmutableList<T> recentCachedData;
                if (loadDataParams.timeSince == 0) {
                    recentCachedData =
                            mMemCache.updateEntitiesCacheFromServer(
                                    loadDataParams.postId,
                                    dataFromServer);
                } else {
                    // Load-more case
                    recentCachedData =
                            mMemCache.updateEntitiesCacheFromServer(
                                    loadDataParams.postId,
                                    loadDataParams.timeSince,
                                    dataFromServer);
                }


                TriState severHasMoreData = TriState.FALSE;
                if (dataFromServer.size() == loadDataParams.maxToFetch) {
                    severHasMoreData = TriState.TRUE;
                }

                LoadDataResult<T> result = LoadDataResult.successResult(
                        recentCachedData,
                        DataSource.SERVER,
                        severHasMoreData);

                Log.d(TAG, "To display data from server for " + mLoadTag);
                return result;
            }
        });

        Futures.addCallback(mLoadDataFuture, new FutureCallback<LoadDataResult<T>>() {
            @Override
            public void onSuccess(LoadDataResult<T> result) {
                Log.d(TAG, "mLoadDataFuture for " + mLoadTag + " succeeded");
                mLoadDataFuture = null;
                notifyDataLoadResult(loadDataParams, result);
                notifyServiceFinish();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "mLoadDataFuture for " + mLoadTag + " failed", t);
                mLoadDataFuture = null;
                LoadDataResult result = LoadDataResult.errorResult(t);
                notifyDataLoadResult(loadDataParams, result);
                notifyServiceFinish();
            }
        });
    }

    /**
     * Load more entities whose sort time is equal or less than 'timeSince'
     * @param timeSince the sort time of the last entity in the list view
     */
    public void loadMoreData(String postId, long timeSince) {
        loadData(new LoadDataParams(
                postId,
                timeSince,
                mMaxFetch,
                DataFreshnessParam.CHECK_SERVER,
                DataLoadCause.UNKNOWN,
                mLoadTag));
    }

    protected abstract void notifyDataLoadResult(
            LoadDataParams loadDataParams,
            LoadDataResult<T> result);
    protected abstract void notifyServiceStart();
    protected abstract void notifyServiceFinish();
    protected abstract ImmutableList<T> loadDataFromServer(
            String postId,
            long timeSince,
            int maxToFetch);
}