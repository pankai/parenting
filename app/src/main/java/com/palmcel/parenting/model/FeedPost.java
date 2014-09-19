package com.palmcel.parenting.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is the class for a user post in feed.
 */
public class FeedPost extends Post {
    public final long timeMsInserted;

    public FeedPost(FeedPostBuilder builder) {
        super(builder);
        timeMsInserted = builder.getTimeMsInserted();
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
        timeMsInserted = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeLong(timeMsInserted);
    }

}
