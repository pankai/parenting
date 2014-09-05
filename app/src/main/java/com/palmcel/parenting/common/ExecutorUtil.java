package com.palmcel.parenting.common;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class that creates ListeningExecutorService.
 */
public class ExecutorUtil {
    private static final ExecutorUtil INSTANCE = new ExecutorUtil();

    private ListeningExecutorService mExecutorService;
    private ListeningExecutorService mNetworkExecutorService;

    private ExecutorUtil() {
        mExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        mNetworkExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));
    }

    /**
     * Execute a short runnable in a thread.
     * @param runnable
     * @return
     */
    public static ListenableFuture execute(Runnable runnable) {
        return INSTANCE.mExecutorService.submit(runnable);
    }

    /**
     * Execute a network operation in a thread.
     * @param runnable
     * @return
     */
    public static ListenableFuture executeNetwork(Runnable runnable) {
        return INSTANCE.mNetworkExecutorService.submit(runnable);
    }
}
