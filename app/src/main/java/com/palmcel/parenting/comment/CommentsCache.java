package com.palmcel.parenting.comment;

import com.palmcel.parenting.cache.EntityCache;
import com.palmcel.parenting.model.PostComment;

/**
 * Singleton class for caching post comments
 */
public class CommentsCache extends EntityCache<PostComment> {

    private static CommentsCache INSTANCE = new CommentsCache();

    private CommentsCache() {
        super("PostComment");
    }

    public static CommentsCache getInstance() {
        return INSTANCE;
    }
}
