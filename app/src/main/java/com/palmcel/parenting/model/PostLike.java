package com.palmcel.parenting.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is the class for a post like or unlike.
 */
public class PostLike implements Parcelable, SortByTimeEntity{
    public final String postId;
    public final String likerUserId;
    public final long timeMsLike;
    public final boolean isLiked;

    public PostLike(PostLikeBuilder builder) {
        postId = builder.getPostId();
        likerUserId = builder.getLikerUserId();
        timeMsLike = builder.getTimeMsLike();
        isLiked = builder.getIsLiked();
    }

    public static final Creator<PostLike> CREATOR = new Creator<PostLike>() {
        @Override
        public PostLike createFromParcel(Parcel in) {
            return new PostLike(in);
        }

        @Override
        public PostLike[] newArray(int size) {
            return new PostLike[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected PostLike(Parcel in) {
        postId = in.readString();
        likerUserId = in.readString();
        timeMsLike = in.readLong();
        isLiked = in.readInt() != 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(postId);
        out.writeString(likerUserId);
        out.writeLong(timeMsLike);
        out.writeInt(isLiked ? 1 : 0);
    }

    @Override
    public long getSortTime() {
        return timeMsLike;
    }
}
