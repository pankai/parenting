package com.palmcel.parenting.model;

import com.palmcel.parenting.login.LoggedInUser;

import java.util.UUID;

/**
 * Builder class for Post.
 */
public class PostBuilder {

    private String postId;
    private String userId;
    private PostType postType;
    // Post category, e.g. 3m, 5y
    private String category;
    // For gender, M or F
    private String forGender;
    // Message of the post
    private String message;
    // Picture url
    private String pictureUrl;
    // external_link_url
    private String externalLinkUrl;
    private String externalLinkImageUrl;
    private String externalLinkCaption;
    private String externalLinkSummary;
    private String productBarCode;
    private PostPublicity publicity;
    private int likes;
    private int comments;
    private boolean isAnonymous;
    private PostStatus postStatus;
    private long timeMsCreated;
    private long timeMsEdited;
    private long timeMsCommented;
    // timestamp of last edited or last commented
    private long timeMsLastUpdated;

    public PostBuilder() {
    }

    public String getPostId() {
        return postId;
    }

    public PostBuilder setPostId(String postId) {
        this.postId = postId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public PostBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public PostType getPostType() {
        return postType;
    }

    public PostBuilder setPostType(PostType postType) {
        this.postType = postType;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public PostBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getForGender() {
        return forGender;
    }

    public PostBuilder setForGender(String forGender) {
        this.forGender = forGender;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public PostBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public PostBuilder setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
        return this;
    }

    public String getExternalLinkUrl() {
        return externalLinkUrl;
    }

    public PostBuilder setExternalLinkUrl(String externalLinkUrl) {
        this.externalLinkUrl = externalLinkUrl;
        return this;
    }

    public String getExternalLinkImageUrl() {
        return externalLinkImageUrl;
    }

    public PostBuilder setExternalLinkImageUrl(String externalLinkImageUrl) {
        this.externalLinkImageUrl = externalLinkImageUrl;
        return this;
    }

    public String getExternalLinkCaption() {
        return externalLinkCaption;
    }

    public PostBuilder setExternalLinkCaption(String externalLinkCaption) {
        this.externalLinkCaption = externalLinkCaption;
        return this;
    }

    public String getExternalLinkSummary() {
        return externalLinkSummary;
    }

    public PostBuilder setExternalLinkSummary(String externalLinkSummary) {
        this.externalLinkSummary = externalLinkSummary;
        return this;
    }

    public String getProductBarCode() {
        return productBarCode;
    }

    public PostBuilder setProductBarCode(String productBarCode) {
        this.productBarCode = productBarCode;
        return this;
    }

    public PostPublicity getPublicity() {
        return publicity;
    }

    public PostBuilder setPublicity(PostPublicity publicity) {
        this.publicity = publicity;
        return this;
    }

    public int getLikes() {
        return likes;
    }

    public PostBuilder setLikes(int likes) {
        this.likes = likes;
        return this;
    }

    public int getComments() {
        return comments;
    }

    public PostBuilder setComments(int comments) {
        this.comments = comments;
        return this;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public PostBuilder setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
        return this;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public PostBuilder setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
        return this;
    }

    public long getTimeMsCreated() {
        return timeMsCreated;
    }

    public PostBuilder setTimeMsCreated(long timeMsCreated) {
        this.timeMsCreated = timeMsCreated;
        return this;
    }

    public long getTimeMsEdited() {
        return timeMsEdited;
    }

    public PostBuilder setTimeMsEdited(long timeMsEdited) {
        this.timeMsEdited = timeMsEdited;
        return this;
    }

    public long getTimeMsCommented() {
        return timeMsCommented;
    }

    public PostBuilder setTimeMsCommented(long timeMsCommented) {
        this.timeMsCommented = timeMsCommented;
        return this;
    }

    public long getTimeMsLastUpdated() {
        return timeMsLastUpdated;
    }

    public PostBuilder setTimeMsLastUpdated(long timeMsLastUpdated) {
        this.timeMsLastUpdated = timeMsLastUpdated;
        return this;
    }

    public Post build() {
        return new Post(this);
    }

    private static String genLocalPostId() {
        return "Local." + UUID.randomUUID().toString();
    }

    public static PostBuilder newLocalRegularPostBuilder(String message, PostSetting postSetting) {
        PostBuilder builder = new PostBuilder();
        builder
            .setPostId(genLocalPostId())
            .setUserId(LoggedInUser.getLoggedInUserId())
            .setPostType(PostType.Regular)
            .setPublicity(postSetting.postPublicity)
            .setPostStatus(PostStatus.Regular)
            .setCategory(postSetting.categoryFrom) //TODO (kpan) only use category from by now
            .setForGender(postSetting.forGender)
            .setIsAnonymous(postSetting.isAnonymous)
            .setMessage(message)
            .setTimeMsCreated(System.currentTimeMillis());

        return builder;
    }
}
