package com.palmcel.parenting.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is the class for a post comment.
 */
public class PostComment implements Parcelable {
    public final String postId;
    public final String commentId;
    public final String postUserId;
    public final String commenterUserId;
    public final String commentMessage;
    public final boolean isAnonymous;
    public final PostStatus commentStatus;
    public final long timeMsCreated;
    public final long timeMsEdited;

    public PostComment(PostCommentBuilder builder) {
        postId = builder.getPostId();
        commentId = builder.getCommentId();
        postUserId = builder.getUserId();
        commenterUserId = builder.getCommenterUserId();
        commentMessage = builder.getCommentMessage();
        isAnonymous = builder.isAnonymous();
        commentStatus = builder.getCommentStatus();
        timeMsCreated = builder.getTimeMsCreated();
        timeMsEdited = builder.getTimeMsEdited();
    }

    public static final Creator<PostComment> CREATOR = new Creator<PostComment>() {
        @Override
        public PostComment createFromParcel(Parcel in) {
            return new PostComment(in);
        }

        @Override
        public PostComment[] newArray(int size) {
            return new PostComment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected PostComment(Parcel in) {
        postId = in.readString();
        commentId = in.readString();
        postUserId = in.readString();
        commenterUserId = in.readString();
        commentMessage = in.readString();
        isAnonymous = in.readInt() != 0;
        commentStatus = PostStatus.valueOf(in.readString());
        timeMsCreated = in.readLong();
        timeMsEdited = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(postId);
        out.writeString(commentId);
        out.writeString(postUserId);
        out.writeString(commenterUserId);
        out.writeString(commentMessage);
        out.writeInt(isAnonymous ? 1 : 0);
        out.writeString(commentStatus.name());
        out.writeLong(timeMsCreated);
        out.writeLong(timeMsEdited);
    }
}
