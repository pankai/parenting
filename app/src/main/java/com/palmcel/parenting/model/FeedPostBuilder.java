package com.palmcel.parenting.model;

/**
 * Builder class for Post in feed.
 */
public class FeedPostBuilder extends PostBuilder {

    private long timeToSort;
    private boolean isLiked;

    public FeedPostBuilder() {
    }

    public FeedPostBuilder from(FeedPost post) {
        setPostId(post.postId);
        setUserId(post.userId);
        setPostType(post.postType);
        setCategory(post.category);
        setForGender(post.forGender);
        setMessage(post.message);
        setPictureUrl(post.pictureUrl);
        setExternalLinkImageUrl(post.externalLinkImageUrl);
        setExternalLinkUrl(post.externalLinkUrl);
        setExternalLinkCaption(post.externalLinkCaption);
        setExternalLinkSummary(post.externalLinkSummary);
        setProductBarCode(post.productBarCode);
        setPublicity(post.publicity);
        setLikes(post.likes);
        setComments(post.comments);
        setIsAnonymous(post.isAnonymous);
        setPostStatus(post.postStatus);
        setTimeMsCreated(post.timeMsCreated);
        setTimeMsEdited(post.timeMsEdited);
        setTimeMsChangeToSurface(post.timeMsChangeToSurface);
        setTimeToSort(post.timeToSort);
        setIsLiked(post.isLiked);

        return this;
    }

    public long getTimeToSort() {
        return timeToSort;
    }

    public FeedPostBuilder setTimeToSort(long timeToSort) {
        this.timeToSort = timeToSort;
        return this;
    }

    public boolean getIsLiked() {
        return isLiked;
    }

    public FeedPostBuilder setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
        return this;
    }

    public FeedPost build() {
        return new FeedPost(this);
    }
}
