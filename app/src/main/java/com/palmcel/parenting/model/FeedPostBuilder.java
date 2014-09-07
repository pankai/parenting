package com.palmcel.parenting.model;

/**
 * Builder class for Post in feed.
 */
public class FeedPostBuilder extends PostBuilder {

    private String feedId;
    private long timeMsInserted;

    public FeedPostBuilder() {
    }

    public String getFeedId() {
        return feedId;
    }

    public FeedPostBuilder setFeedId(String feedId) {
        this.feedId = feedId;
        return this;
    }

    public long getTimeMsInserted() {
        return timeMsInserted;
    }

    public FeedPostBuilder setTimeMsInserted(long timeMsInserted) {
        this.timeMsInserted = timeMsInserted;
        return this;
    }

    public FeedPost build() {
        return new FeedPost(this);
    }
}
