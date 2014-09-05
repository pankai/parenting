package com.palmcel.parenting.common;

import android.os.Handler;
import android.os.Looper;

/**
 * Util class for Android handler
 */
public class HandlerUtil {
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public void runOnUiThread(Runnable runnable) {
       sHandler.post(runnable);
    }

    public static Handler getHandler() {
        return sHandler;
    }
}
