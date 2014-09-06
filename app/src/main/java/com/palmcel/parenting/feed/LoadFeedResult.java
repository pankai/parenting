package com.palmcel.parenting.feed;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.model.Post;

/**
 * Result of loading feed
 */
public class LoadFeedResult {
    boolean isSuccess;
    Throwable error;
    ImmutableList<Post> posts;

    public LoadFeedResult(boolean isSuccess, Throwable err, ImmutableList<Post> posts) {
        this.isSuccess = isSuccess;
        this.error = err;
        this.posts = posts;
    }

    public static LoadFeedResult errorResult(Throwable err) {
        return new LoadFeedResult(false, err, null);
    }
}
