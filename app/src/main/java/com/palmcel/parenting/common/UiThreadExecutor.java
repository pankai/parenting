package com.palmcel.parenting.common;

import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * Executor running on UI thread
 */
public class UiThreadExecutor implements Executor {
    private final Handler mHandler = HandlerUtil.getHandler();

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }
}
