package com.palmcel.parenting.model;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.DataSource;

/**
 * Result of loading feed or comments from cache, db or server
 */
public class LoadDataResult<T> {
    public boolean isSuccess;
    public Throwable error;
    public ImmutableList<T> loadedData;
    public DataSource dataSource;

    public LoadDataResult(
            boolean isSuccess,
            Throwable err,
            ImmutableList<T> loadedData,
            DataSource dataSource) {
        this.isSuccess = isSuccess;
        this.error = err;
        this.loadedData = loadedData;
        this.dataSource = dataSource;
    }

    public LoadDataResult(boolean isSuccess, Throwable err, ImmutableList<T> loadedData) {
        this.isSuccess = isSuccess;
        this.error = err;
        this.loadedData = loadedData;
        this.dataSource = DataSource.UNKNOWN; //TODO
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
        return new LoadDataResult(true, null, loadedData, dataSource);
    }

    public boolean isEmpty() {
        return loadedData.isEmpty();
    }
}
