package com.palmcel.parenting.db;

import android.provider.BaseColumns;

/**
 * Database in this app
 */
public class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {
    }

    /* Inner class that defines the post fields */
    public static abstract class PostEntry implements BaseColumns {
        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_POST_TYPE = "post_type";
        public static final String COLUMN_POST_CATEGORY = "post_category";
        public static final String COLUMN_FOR_GENDER = "for_gender";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_PICTURE_URL = "picture_url";
        public static final String COLUMN_EXT_LINK_URL = "external_link_url";
        public static final String COLUMN_EXT_LINK_IMAGE_URL = "external_link_image_url";
        public static final String COLUMN_EXT_LINK_CAPTION = "external_link_caption";
        public static final String COLUMN_EXT_LINK_SUMMARY = "external_link_summary";
        public static final String COLUMN_PRODUCT_BAR_CODE = "product_bar_code";
        public static final String COLUMN_PUBLICITY = "publicity";
        public static final String COLUMN_QUESTION_POINTS = "question_points";
        public static final String COLUMN_LIKES = "likes";
        public static final String COLUMN_COMMENTS = "comments";
        public static final String COLUMN_IS_ANONYMOUS = "is_anonymous";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TIME_CREATED = "time_ms_created";
        public static final String COLUMN_TIME_EDITED = "time_ms_edited";
        public static final String COLUMN_TIME_CHANGE_TO_SURFACE = "time_ms_change_to_surface";
    }

    /* Inner class that defines the feed_post table contents in addition to post fields */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed_post";
        public static final String COLUMN_TIME_SORT = "time_to_sort";
        public static final String COLUMN_IS_LIKED = "is_liked";
    }

    public static String FEED_COLUMNS_STRING =
            PostEntry.COLUMN_POST_ID + ", " +
            FeedEntry.COLUMN_TIME_SORT + ", " +
            FeedEntry.COLUMN_IS_LIKED + ", " +
            PostEntry.COLUMN_USER_ID + ", " +
            PostEntry.COLUMN_POST_TYPE + ", " +
            PostEntry.COLUMN_POST_CATEGORY + ", " +
            PostEntry.COLUMN_MESSAGE + ", " +
            PostEntry.COLUMN_PICTURE_URL + ", " +
            PostEntry.COLUMN_EXT_LINK_URL + ", " +
            PostEntry.COLUMN_EXT_LINK_IMAGE_URL + ", " +
            PostEntry.COLUMN_EXT_LINK_CAPTION + ", " +
            PostEntry.COLUMN_EXT_LINK_SUMMARY + ", " +
            PostEntry.COLUMN_PRODUCT_BAR_CODE + ", " +
            PostEntry.COLUMN_PUBLICITY + ", " +
            PostEntry.COLUMN_LIKES + ", " +
            PostEntry.COLUMN_COMMENTS + ", " +
            PostEntry.COLUMN_IS_ANONYMOUS + ", " +
            PostEntry.COLUMN_STATUS + ", " +
            PostEntry.COLUMN_TIME_CREATED + ", " +
            PostEntry.COLUMN_TIME_EDITED + ", " +
            PostEntry.COLUMN_TIME_CHANGE_TO_SURFACE;
}