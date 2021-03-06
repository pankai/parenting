package com.palmcel.parenting.model;

import com.palmcel.parenting.login.LoggedInUser;

/**
 * Builder class for PostLike.
 */
public class PostLikeBuilder {

    private String postId;
    private String likerUserId;
    private long timeMsLike;
    private boolean isLiked;

    public PostLikeBuilder() {
    }

    public String getPostId() {
        return postId;
    }

    public PostLikeBuilder setPostId(String postId) {
        this.postId = postId;
        return this;
    }

    public String getLikerUserId() {
        return likerUserId;
    }

    public PostLikeBuilder setLikerUserId(String likerUserId) {
        this.likerUserId = likerUserId;
        return this;
    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public PostLikeBuilder setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
        return this;
    }

    public long getTimeMsLike() {
        return timeMsLike;
    }

    public PostLikeBuilder setTimeMsLike(long timeMsLike) {
        this.timeMsLike = timeMsLike;
        return this;
    }

    public PostLike build() {
        return new PostLike(this);
    }

    public static PostLikeBuilder newLocalLikeBuilder(
            String postId, boolean isLiked) {
        PostLikeBuilder builder = new PostLikeBuilder();
        builder
            .setPostId(postId)
            .setLikerUserId(LoggedInUser.getLoggedInUserId())
            .setIsLiked(isLiked);

        return builder;
    }
}
