package com.palmcel.parenting.model;

import android.os.Parcel;
import android.os.Parcelable;

import junit.framework.Assert;

/**
 * This is the class for a user post.
 */
public class Post implements Parcelable {
    public final String postId;
    public final String userId;
    public final PostType postType;
    // Post category, e.g. 3m, 5y
    public final String category;
    // For gender, F or M
    public final String forGender;
    // Message of the post
    public final String message;
    // Picture url
    public final String pictureUrl;
    // external_link_url
    public final String externalLinkUrl;
    public final String externalLinkImageUrl;
    public final String externalLinkCaption;
    public final String externalLinkSummary;
    public final String productBarCode;
    public final PostPublicity publicity;
    // Like count
    public final int likes;
    // Comment count
    public final int comments;
    public final boolean isAnonymous;
    public final PostStatus postStatus;
    public final long timeMsCreated;
    public final long timeMsEdited;
    public final long timeMsCommented;
    // timestamp of last edited or last commented
    public final long timeMsLastUpdated;

    public Post(PostBuilder builder) {
        postId = builder.getPostId();
        userId = builder.getUserId();
        postType = builder.getPostType();
        category = builder.getCategory();
        forGender = builder.getForGender();
        message = builder.getMessage();
        pictureUrl = builder.getPictureUrl();
        externalLinkUrl = builder.getExternalLinkUrl();
        externalLinkImageUrl = builder.getExternalLinkImageUrl();
        externalLinkCaption = builder.getExternalLinkCaption();
        externalLinkSummary = builder.getExternalLinkSummary();
        productBarCode = builder.getProductBarCode();
        publicity = builder.getPublicity();
        likes = builder.getLikes();
        comments = builder.getComments();
        isAnonymous = builder.isAnonymous();
        postStatus = builder.getPostStatus();
        timeMsCreated = builder.getTimeMsCreated();
        timeMsEdited = builder.getTimeMsEdited();
        timeMsCommented = builder.getTimeMsCommented();
        timeMsLastUpdated = builder.getTimeMsLastUpdated();

        assertInstance();
    }

    private void assertInstance() {
        Assert.assertNotNull(postId);
        Assert.assertNotNull(userId);
        Assert.assertNotNull(postType);
        Assert.assertNotNull(publicity);
        Assert.assertNotNull(postStatus);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected Post(Parcel in) {
        postId = in.readString();
        userId = in.readString();
        postType = PostType.valueOf(in.readString());
        category = in.readString();
        forGender = in.readString();
        message = in.readString();
        pictureUrl = in.readString();
        externalLinkUrl = in.readString();
        externalLinkImageUrl = in.readString();
        externalLinkCaption = in.readString();
        externalLinkSummary = in.readString();
        productBarCode = in.readString();
        publicity = PostPublicity.valueOf(in.readString());
        likes = in.readInt();
        comments = in.readInt();
        isAnonymous = in.readInt() != 0;
        postStatus = PostStatus.valueOf(in.readString());
        timeMsCreated = in.readLong();
        timeMsEdited = in.readLong();
        timeMsCommented = in.readLong();
        timeMsLastUpdated = in.readLong();

        assertInstance();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(postId);
        out.writeString(userId);
        out.writeString(postType.name());
        out.writeString(category);
        out.writeString(forGender);
        out.writeString(message);
        out.writeString(pictureUrl);
        out.writeString(externalLinkUrl);
        out.writeString(externalLinkImageUrl);
        out.writeString(externalLinkCaption);
        out.writeString(externalLinkSummary);
        out.writeString(productBarCode);
        out.writeString(publicity.name());
        out.writeInt(likes);
        out.writeInt(comments);
        out.writeInt(isAnonymous ? 1 : 0);
        out.writeString(postStatus.name());
        out.writeLong(timeMsCreated);
        out.writeLong(timeMsEdited);
        out.writeLong(timeMsCommented);
        out.writeLong(timeMsLastUpdated);
    }

}
