package com.palmcel.parenting.model;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.DataSource;

/**
 * Result of loading feed
 */
public class LoadFeedResult {
    public boolean isSuccess;
    public Throwable error;
    public ImmutableList<FeedPost> feedPosts;
    public DataSource dataSource;

    public LoadFeedResult(
            boolean isSuccess,
            Throwable err,
            ImmutableList<FeedPost> feedPosts,
            DataSource dataSource) {
        this.isSuccess = isSuccess;
        this.error = err;
        this.feedPosts = feedPosts;
        this.dataSource = dataSource;
    }

    public LoadFeedResult(boolean isSuccess, Throwable err, ImmutableList<FeedPost> feedPosts) {
        this.isSuccess = isSuccess;
        this.error = err;
        this.feedPosts = feedPosts;
        this.dataSource = DataSource.UNKNOWN; //TODO
    }

    public static LoadFeedResult errorResult(Throwable err) {
        return new LoadFeedResult(false, err, null);
    }

    public static LoadFeedResult successResult(ImmutableList<FeedPost> feedPosts) {
        return new LoadFeedResult(true, null, feedPosts);
    }

    public static LoadFeedResult successResult(
            ImmutableList<FeedPost> feedPosts,
            DataSource dataSource) {
        return new LoadFeedResult(true, null, feedPosts, dataSource);
    }

    public boolean isEmpty() {
        return feedPosts.isEmpty();
    }
}
