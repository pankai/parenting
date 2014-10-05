package com.palmcel.parenting.cache;

import android.text.format.DateUtils;

/**
 * Singleton that caches the user feed
 */
public class FeedCache extends BaseFeedCache {

    private static final String TAG = "FeedCache";
    private static final FeedCache INSTANCE = new FeedCache();
    private static final long sStaleIntervalMs = 3 * DateUtils.MINUTE_IN_MILLIS;

    private FeedCache() {
        super(TAG, sStaleIntervalMs);
    }

    public static FeedCache getInstance() {
        return INSTANCE;
    }
}
