package com.palmcel.parenting.likes;

import com.palmcel.parenting.cache.EntityCache;
import com.palmcel.parenting.model.PostLike;

/**
 * Singleton class for caching post likes
 */
public class LikesCache extends EntityCache<PostLike> {

    private static LikesCache INSTANCE = new LikesCache();

    private LikesCache() {
        super("PostLike");
    }

    public static LikesCache getInstance() {
        return INSTANCE;
    }
}
