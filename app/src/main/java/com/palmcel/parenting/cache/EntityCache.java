package com.palmcel.parenting.cache;

import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.LruCache;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.SortByTimeEntity;

/**
 * Generic cache class for caching comments, likes, etc, and the logic for updating them.
 */
public class EntityCache<T extends SortByTimeEntity> {

    private static final String TAG = "EntityCache";
    private static final long STALE_THRESHOLD = DateUtils.MINUTE_IN_MILLIS;

    private static final int MAX_CACHE_SIZE = 100;

    private static class EntitiesWithUpdateTime<T> {
        private ImmutableList<T> entities;
        // The time the most recent entities are updated from server data.
        private long updatedFromServerTimeMs;

        private EntitiesWithUpdateTime(ImmutableList<T> entities) {
            this.entities = entities;
            this.updatedFromServerTimeMs = System.currentTimeMillis();
        }

        private EntitiesWithUpdateTime(ImmutableList<T> entities, long updatedFromServerTimeMs) {
            this.entities = entities;
            this.updatedFromServerTimeMs = updatedFromServerTimeMs;
        }
    }

    // Map<post id, List of entities, e.g. comments, likes sorted by getSortTime in DESC order>
    private LruCache<String, EntitiesWithUpdateTime<T>> mEntitiesCache;

    // Used for logging
    private String mEntityTag;

    public EntityCache(String entityTag) {
        mEntitiesCache = new LruCache<String, EntitiesWithUpdateTime<T>>(MAX_CACHE_SIZE);
        mEntityTag = entityTag;
    }

    /**
     * @param cacheKey e.g post id for comments cache or likes cache
     * @return For comments cache, if the cached comments for postId exists and is up-to-date,
     * return it. Otherwise return null.
     */
    @Nullable
    public synchronized ImmutableList<T> getEntitiesIfUpToDate(String cacheKey) {
        EntitiesWithUpdateTime entitiesWithUpdateTime = mEntitiesCache.get(cacheKey);

        if (entitiesWithUpdateTime == null ||
                System.currentTimeMillis() - entitiesWithUpdateTime.updatedFromServerTimeMs >
                        STALE_THRESHOLD) {
            return null;
        } else {
            return entitiesWithUpdateTime.entities;
        }
    }

    /**
     * Update mEntitiesCache with the latest entities, e.g. comments, likes, loaded from server
     * @param cacheKey e.g. the post id
     * @param entitiesFromServer e.g for comments cache, it is post comments from server.
     *                           The comments starts from the latest
     *                           comments. It is not middle portion in the post comments. Comments
     *                           are sorted by timeMsCreated in DESC order in commentsFromServer.
     * @return updated entities from cache for the post
     */
    public synchronized ImmutableList<T> updateEntitiesCacheFromServer(
            String cacheKey,
            ImmutableList<T> entitiesFromServer) {
        Log.d(TAG, "In updateEntitiesCacheFromServer for " + mEntityTag +
                ", mEntitiesCache=" + mEntitiesCache.size() +
                ", entitiesFromServer=" + entitiesFromServer.size() +
                ", cacheKey=" + cacheKey);

        EntitiesWithUpdateTime<T> entitiesWithTime = mEntitiesCache.get(cacheKey);
        ImmutableList<T> entitiesInCache =
                entitiesWithTime == null ? null : entitiesWithTime.entities;

        if (entitiesInCache == null ||
                entitiesInCache.isEmpty() ||
                entitiesFromServer.isEmpty()) {
            mEntitiesCache.put(cacheKey, new EntitiesWithUpdateTime<T>(entitiesFromServer));
            return entitiesFromServer;
        }

        ImmutableList.Builder<T> builder = ImmutableList.builder();
        builder.addAll(entitiesFromServer);

        T lastInServer = entitiesFromServer.get(entitiesFromServer.size() - 1);

        boolean hasFoundLast = false;

        for (T entity: entitiesInCache) {
            if (hasFoundLast) {
                builder.add(entity);
            } else if (entity.getSortTime() == lastInServer.getSortTime()) {
                hasFoundLast = true;
            } else if (entity.getSortTime() < lastInServer.getSortTime()) {
                // There is hole between memory cache and entityFromServer.
                Log.w(TAG,
                        "Warning, there is a hole between memory cache and entityFromServer for " +
                                mEntityTag);
                mEntitiesCache.put(cacheKey, new EntitiesWithUpdateTime<T>(entitiesFromServer));
                return entitiesFromServer;
            }
        }

        ImmutableList<T> newEntities = builder.build();
        mEntitiesCache.put(cacheKey, new EntitiesWithUpdateTime<T>(newEntities));

        return newEntities;
    }

    /**
     * Update mEntitiesCache with the load-more entities from server
     * @param sortTimeSince, the smallest sort time of the entities at client before
     *                             loading more
     * @param entitiesFromServer for comments cache. It is comments from server. The comments are
     *                           a result of load more.
     *                           That is, they don't start with the latest comment in the post
     *                           at server.
     * @return updated entities from cache
     */
    public synchronized ImmutableList<T> updateEntitiesCacheFromServer(
            String cacheKey,
            long sortTimeSince,
            ImmutableList<T> entitiesFromServer) {
        Log.d(TAG, "In updateEntitiesCacheFromServer for load-more " + mEntityTag +
                ", mEntitiesCache=" + mEntitiesCache.size() +
                ", entitiesFromServer=" + entitiesFromServer.size() +
                ", sortTimeSince=" + sortTimeSince +
                ", cacheKey=" + cacheKey);

        EntitiesWithUpdateTime<T> entitiesWithTime = mEntitiesCache.get(cacheKey);
        ImmutableList<T> entitiesInCache =
                entitiesWithTime == null ? null : entitiesWithTime.entities;

        if (entitiesInCache == null || entitiesInCache.isEmpty()) {
            mEntitiesCache.put(cacheKey, new EntitiesWithUpdateTime<T>(entitiesFromServer, 0));
            Log.e(TAG, "mEntitiesCache became empty for entity, " + cacheKey +
                    " after loading more for " + mEntityTag, new RuntimeException());
            return entitiesFromServer;
        }
        if (entitiesFromServer.isEmpty()) {
            Log.d(TAG,
                    "updateCommentsCacheFromServer for load-more, got 0 entities from server for " +
                            mEntityTag);
            return entitiesInCache;
        }

        T lastInMemory = entitiesInCache.get(entitiesInCache.size() - 1);
        T firstFromServer = entitiesFromServer.get(0);

        if (sortTimeSince != firstFromServer.getSortTime()) {
            Log.e(TAG, "Inconsistent sortTimeSince after loading more entities, " +
                    sortTimeSince + " vs " +
                    firstFromServer.getSortTime() +
                    " for " + mEntityTag, new RuntimeException());
            return entitiesInCache;
        }

        if (lastInMemory.getSortTime() != firstFromServer.getSortTime()) {
            Log.e(TAG, "Unmatched getSortTime after loading more comments, " +
                    lastInMemory.getSortTime() + " vs " +
                    firstFromServer.getSortTime() +
                    " for " + mEntityTag, new RuntimeException());
            return entitiesInCache;
        }

        ImmutableList.Builder<T> builder = ImmutableList.builder();
        builder.addAll(entitiesInCache.subList(0, entitiesInCache.size() - 1));
        builder.addAll(entitiesFromServer);

        ImmutableList<T> newEntities = builder.build();
        // Don't change the EntitiesWithUpdateTime.updatedFromServerTimeMs because it is load more.
        mEntitiesCache.put(
                cacheKey,
                new EntitiesWithUpdateTime<T>(
                        newEntities, entitiesWithTime.updatedFromServerTimeMs));

        return newEntities;
    }
}
