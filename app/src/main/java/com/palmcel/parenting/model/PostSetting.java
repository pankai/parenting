package com.palmcel.parenting.model;

import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;

/**
 * Publishing setting for an individual post
 */
public class PostSetting {
    public PostPublicity postPublicity;
    public boolean isAnonymous;
    public String forGender;
    public String categoryFrom;
    public String categoryTo;

    public PostSetting(
            PostPublicity postPublicity,
            boolean isAnonymous,
            @Nullable String forGender,
            @Nullable String categoryFrom,
            @Nullable String categoryTo) {
        this.postPublicity = postPublicity;
        this.isAnonymous = isAnonymous;
        this.forGender = forGender;
        this.categoryFrom = categoryFrom;
        this.categoryTo = categoryTo;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
            .add("postPublicity", postPublicity)
            .add("isAnonymous", isAnonymous)
            .add("forGender", forGender)
            .add("categoryFrom", categoryFrom)
            .add("categoryTo", categoryTo)
            .toString();
    }
}
