package com.palmcel.parenting.model;

import com.palmcel.parenting.login.LoggedInUser;

/**
 * Builder class for PostComment.
 */
public class PostCommentBuilder {

    private String postId;
    private String commentId;
    private String postUserId;
    private String commenterUserId;
    private String commentMessage;
    private boolean isAnonymous;
    private PostStatus commentStatus;
    private long timeMsCreated;
    private long timeMsEdited;

    public PostCommentBuilder() {
    }

    public String getPostId() {
        return postId;
    }

    public PostCommentBuilder setPostId(String postId) {
        this.postId = postId;
        return this;
    }

    public String getCommentId() {
        return commentId;
    }

    public PostCommentBuilder setCommentId(String commentId) {
        this.commentId = commentId;
        return this;
    }

    public String getUserId() {
        return postUserId;
    }

    public PostCommentBuilder setPostUserId(String userId) {
        this.postUserId = userId;
        return this;
    }

    public String getCommenterUserId() {
        return commenterUserId;
    }

    public PostCommentBuilder setCommenterUserId(String commenterUserId) {
        this.commenterUserId = commenterUserId;
        return this;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public PostCommentBuilder setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
        return this;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public PostCommentBuilder setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
        return this;
    }

    public PostStatus getCommentStatus() {
        return commentStatus;
    }

    public PostCommentBuilder setCommentStatus(PostStatus commentStatus) {
        this.commentStatus = commentStatus;
        return this;
    }

    public long getTimeMsCreated() {
        return timeMsCreated;
    }

    public PostCommentBuilder setTimeMsCreated(long timeMsCreated) {
        this.timeMsCreated = timeMsCreated;
        return this;
    }

    public long getTimeMsEdited() {
        return timeMsEdited;
    }

    public PostCommentBuilder setTimeMsEdited(long timeMsEdited) {
        this.timeMsEdited = timeMsEdited;
        return this;
    }

    public PostComment build() {
        return new PostComment(this);
    }

    public static PostCommentBuilder newLocalRegularCommentBuilder(
            String message,
            PostSetting postSetting) {
        PostCommentBuilder builder = new PostCommentBuilder();
        builder
            .setPostUserId(LoggedInUser.getLoggedInUserId())
            .setCommenterUserId(LoggedInUser.getLoggedInUserId())
            .setCommentStatus(PostStatus.Normal)
            .setIsAnonymous(postSetting.isAnonymous)
            .setCommentMessage(message)
            .setTimeMsCreated(System.currentTimeMillis());

        return builder;
    }
}
