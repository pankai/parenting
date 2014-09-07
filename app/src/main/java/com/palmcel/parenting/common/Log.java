package com.palmcel.parenting.common;

/**
 * Custom logging
 */
public class Log {
    private static final String APP_TAG = "PZ:";

    public final static boolean LOGGING_ERROR = true;
    public final static boolean LOGGING_WARNING = true;
    public final static boolean LOGGING_DEBUG = true;
    public final static boolean LOGGING_INFO = true;

    static public void e(String tag, String message) {
        if (LOGGING_ERROR) {
            android.util.Log.e(APP_TAG + tag, message);
        }
    }

    static public void e(String tag, String message, Throwable t) {
        if (LOGGING_ERROR) {
            android.util.Log.e(APP_TAG + tag, message, t);
        }
    }

    static public void w(String tag, String message) {
        if (LOGGING_WARNING) {
            android.util.Log.w(APP_TAG + tag, message);
        }
    }

    static public void d(String tag, String message) {
        if (LOGGING_DEBUG) {
            android.util.Log.d(APP_TAG + tag, message);
        }
    }

    static public void i(String tag, String message) {
        if (LOGGING_INFO) {
            android.util.Log.i(tag, message);
        }
    }
}
