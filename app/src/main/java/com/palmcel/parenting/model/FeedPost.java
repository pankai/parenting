package com.palmcel.parenting.model;

import android.os.Parcel;

/**
 * This is the class for a user post in feed.
 */
public class FeedPost extends Post {
    public final long timeToSort;
    public final boolean isLiked;

    public FeedPost(FeedPostBuilder builder) {
        super(builder);
        timeToSort = builder.getTimeToSort();
        isLiked = builder.getIsLiked();
    }

    public static final Creator<FeedPost> CREATOR = new Creator<FeedPost>() {
        @Override
        public FeedPost createFromParcel(Parcel in) {
            return new FeedPost(in);
        }

        @Override
        public FeedPost[] newArray(int size) {
            return new FeedPost[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private FeedPost(Parcel in) {
        super(in);
        timeToSort = in.readLong();
        isLiked = in.readInt() != 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeLong(timeToSort);
        out.writeInt(isLiked ? 1 : 0);
    }

}
