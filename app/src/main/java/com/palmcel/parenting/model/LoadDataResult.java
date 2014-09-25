package com.palmcel.parenting.model;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.DataSource;
import com.palmcel.parenting.common.TriState;

/**
 * Result of loading feed or comments from cache, db or server
 */
public class LoadDataResult<T> {
    public boolean isSuccess;
    public Throwable error;
    public ImmutableList<T> loadedData;
    public DataSource dataSource;
    // Whether server has more data to load
    public TriState serverHasMore = TriState.NOT_SET;

    public LoadDataResult(
            boolean isSuccess,
            Throwable err,
            ImmutableList<T> loadedData,
            DataSource dataSource,
            TriState serverHasMore) {
        this.isSuccess = isSuccess;
        this.error = err;
        this.loadedData = loadedData;
        this.dataSource = dataSource;
        this.serverHasMore = serverHasMore;
    }

    public LoadDataResult(boolean isSuccess, Throwable err, ImmutableList<T> loadedData) {
        this.isSuccess = isSuccess;
        this.error = err;
        this.loadedData = loadedData;
        this.dataSource = DataSource.UNKNOWN; //TODO
        this.serverHasMore = TriState.NOT_SET;
    }

    public static LoadDataResult errorResult(Throwable err) {
        return new LoadDataResult(false, err, null);
    }

    public static <T> LoadDataResult<T> successResult(ImmutableList<T> loadedData) {
        return new LoadDataResult(true, null, loadedData);
    }

    public static <T> LoadDataResult<T> successResult(
            ImmutableList<T> loadedData,
            DataSource dataSource) {
        return new LoadDataResult(true, null, loadedData, dataSource, TriState.NOT_SET);
    }

    public static <T> LoadDataResult<T> successResult(
            ImmutableList<T> loadedData,
            DataSource dataSource,
            TriState serverHasMore) {
        return new LoadDataResult(true, null, loadedData, dataSource, serverHasMore);
    }

    public boolean isEmpty() {
        return loadedData.isEmpty();
    }
}
