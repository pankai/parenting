package com.palmcel.parenting.model;

import com.google.common.collect.ImmutableList;

/**
 * Result of loading feed
 */
public class LoadFeedResult {
    public boolean isSuccess;
    public Throwable error;
    public ImmutableList<FeedPost> feedPosts;

    public LoadFeedResult(boolean isSuccess, Throwable err, ImmutableList<FeedPost> feedPosts) {
        this.isSuccess = isSuccess;
        this.error = err;
        this.feedPosts = feedPosts;
    }

    public static LoadFeedResult errorResult(Throwable err) {
        return new LoadFeedResult(false, err, null);
    }

    public static LoadFeedResult successResult(ImmutableList<FeedPost> feedPosts) {
        return new LoadFeedResult(true, null, feedPosts);
    }
}
