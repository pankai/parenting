package com.palmcel.parenting.cache;

import android.text.format.DateUtils;

/**
 * Singleton that caches the category feed
 */
public class CategoryFeedCache extends BaseFeedCache {

    private static final String TAG = "CategoryFeedCache";
    private static final CategoryFeedCache INSTANCE = new CategoryFeedCache();
    private static final long sStaleIntervalMs = 1 * DateUtils.MINUTE_IN_MILLIS;

    private CategoryFeedCache() {
        super(TAG, sStaleIntervalMs);
    }

    public static CategoryFeedCache getInstance() {
        return INSTANCE;
    }
}
