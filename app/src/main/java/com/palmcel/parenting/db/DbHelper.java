package com.palmcel.parenting.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.palmcel.parenting.common.AppContext;

import static com.palmcel.parenting.db.DatabaseContract.PostEntry;
import static com.palmcel.parenting.db.DatabaseContract.FeedEntry;

/**
 * OpenHelper for database
 */
public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "parenting";

    private static final String SQL_CREATE_FEED_POST_TABLE =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                PostEntry.COLUMN_POST_ID + " TEXT PRIMARY KEY, " +
                PostEntry.COLUMN_USER_ID + " TEXT, " +
                PostEntry.COLUMN_POST_TYPE + " TEXT, " +
                PostEntry.COLUMN_POST_CATEGORY + " TEXT, " +
                PostEntry.COLUMN_FOR_GENDER + " TEXT, " +
                PostEntry.COLUMN_MESSAGE + " TEXT, " +
                PostEntry.COLUMN_PICTURE_URL + " TEXT, " +
                PostEntry.COLUMN_EXT_LINK_URL + " TEXT, " +
                PostEntry.COLUMN_EXT_LINK_IMAGE_URL + " TEXT, " +
                PostEntry.COLUMN_EXT_LINK_CAPTION + " TEXT, " +
                PostEntry.COLUMN_EXT_LINK_SUMMARY + " TEXT, " +
                PostEntry.COLUMN_PRODUCT_BAR_CODE + " TEXT, " +
                PostEntry.COLUMN_PUBLICITY + " TEXT, " +
                PostEntry.COLUMN_QUESTION_POINTS + " INTEGER, " +
                PostEntry.COLUMN_LIKES + " INTEGER, " +
                PostEntry.COLUMN_COMMENTS + " INTEGER, " +
                PostEntry.COLUMN_IS_ANONYMOUS + " INTEGER, " +
                PostEntry.COLUMN_STATUS + " TEXT, " +
                PostEntry.COLUMN_TIME_CREATED + " INTEGER, " +
                PostEntry.COLUMN_TIME_EDITED + " INTEGER, " +
                PostEntry.COLUMN_TIME_CHANGE_TO_SURFACE + " INTEGER, " +
                FeedEntry.COLUMN_TIME_SORT + " INTEGER, " +
                FeedEntry.COLUMN_IS_LIKED + " INTEGER " +
            ")";

    private static final String SQL_DELETE_FEED_POST_TABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private static final String SQL_CREATE_FEED_TABLE_INDEX =
            "CREATE INDEX feed_time_idx ON " +
                    FeedEntry.TABLE_NAME + "(" + FeedEntry.COLUMN_TIME_SORT + ")";

    private static DbHelper HELPER_INSTANCE;
    private static SQLiteDatabase DB_INSTANCE;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FEED_POST_TABLE);
        db.execSQL(SQL_CREATE_FEED_TABLE_INDEX);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_FEED_POST_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Make DBHelper instance and SQLiteDatabase instance singletons to for lock issue during
     * concurrency.
     * See discussions in http://stackoverflow.com/questions/2493331/what-are-the-best-practices-for-sqlite-on-android.
     * @return a singleton SQLiteDatabase instance.
     */
    public static synchronized SQLiteDatabase getDb() {
        if (DB_INSTANCE != null) {
            return DB_INSTANCE;
        }
        if (HELPER_INSTANCE == null) {
            HELPER_INSTANCE = new DbHelper(AppContext.getAplicationContext());
        }

        DB_INSTANCE = HELPER_INSTANCE.getWritableDatabase();
        return DB_INSTANCE;
    }
}
