package com.palmcel.parenting.feed;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.model.FeedPost;

/**
 * Result of loading feed
 */
public class LoadFeedResult {
    boolean isSuccess;
    Throwable error;
    ImmutableList<FeedPost> feedPosts;

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
