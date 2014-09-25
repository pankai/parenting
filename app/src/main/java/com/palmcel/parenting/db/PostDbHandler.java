package com.palmcel.parenting.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.FeedPost;

/**
 * Singleton class. Db operations for feed post.
 */
public class PostDbHandler {

    private static final String TAG = "PostDbHandler";
    private static PostDbHandler INSTANCE = new PostDbHandler();

    private PostDbHandler() {}


    /**
     * Update feed_post table with most recent feed loaded from server
     * @param feedFromServer feed from server. The feed starts from the latest post in the feed. It
     *                       is not middle portion in the feed.
     */
    public void updateFeedPostTable(ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateFeedPostTable, feedFromServer=" + feedFromServer.size());

        SQLiteDatabase db = DbHelper.getDb();
        if (feedFromServer.isEmpty()) {
            // Clear feed_post table
            db.delete(DatabaseContract.FeedEntry.TABLE_NAME, null, null);
            return;
        }

        FeedPost lastInServerFeed = feedFromServer.get(feedFromServer.size() - 1);

        db.beginTransaction();

        try {
            // Check whether feed_post table contains lastInServerFeed
            if (feedPostTableContains(lastInServerFeed.postId, lastInServerFeed.timeMsInserted)) {
                Log.d(TAG, "updateFeedPostTable, feed_post table contains post (" +
                        lastInServerFeed.postId + ", " + lastInServerFeed.timeMsInserted + ")");

                // Delete posts before lastInServerFeed.timeMsInserted in feed_post table
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        DatabaseContract.FeedEntry.COLUMN_TIME_INSERTED + " >= ?",
                        new String[]{Long.toString(lastInServerFeed.timeMsInserted)}
                );
            } else {
                Log.d(TAG, "updateFeedPostTable, feed_post table doesn't contain post (" +
                        lastInServerFeed.postId + ", " + lastInServerFeed.timeMsInserted + ")");
                // Clear feed_post table
                db.delete(DatabaseContract.FeedEntry.TABLE_NAME, null, null);
            }

            // Insert feedFromServer into feed_post table
            insertIntoFeedPostTable(db, feedFromServer);

            db.setTransactionSuccessful();
            Log.d(TAG, "updateFeedPostTable succeeded");
        } catch (Exception ex) {
            Log.e(TAG, "Exception in updateFeedPostTable", ex);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Update feed_post table with the load-more feed from server
     * @param timeMsInsertedSince the load-more feed is loaded from timeMsInsertedSince and older
     *                            than it.
     * @param feedFromServer feed from server sorted by timeMsInserted in DESC order.
     *                       The feed is a result of load more. That is, it
     *                       doesn't start with the latest post in the feed at server.
     */
    public void updateFeedPostTable(
            long timeMsInsertedSince,
            ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateFeedPostTable for load-more, timeMsInsertedSince=" +
                timeMsInsertedSince + ", feedFromServer=" + feedFromServer.size());

        if (feedFromServer.isEmpty()) {
            Log.d(TAG, "updateFeedPostTable for load-more, empty feed from server");
            return;
        }

        FeedPost firstInServerFeed = feedFromServer.get(0);

        if (!feedPostTableContains(firstInServerFeed.postId, firstInServerFeed.timeMsInserted)) {
            Log.d(TAG, "updateFeedPostTable for load-more, feed_post table doesn't contain " +
                    firstInServerFeed.timeMsInserted);
            return;
        }

        FeedPost lastInServerFeed = feedFromServer.get(feedFromServer.size() - 1);

        SQLiteDatabase db = DbHelper.getDb();
        db.beginTransaction();

        try {
            // Check whether feed_post table contains lastInServerFeed
            if (feedPostTableContains(lastInServerFeed.postId, lastInServerFeed.timeMsInserted)) {
                Log.d(TAG, "updateFeedPostTable for load-more, delete posts from table between " +
                        firstInServerFeed.timeMsInserted +
                        " and " + lastInServerFeed.timeMsInserted);

                // Delete posts between firstInServerFeed.timeMsInserted and
                // lastInServerFeed.timeMsInserted in feed_post table
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        DatabaseContract.FeedEntry.COLUMN_TIME_INSERTED + " <= ? AND " +
                                DatabaseContract.FeedEntry.COLUMN_TIME_INSERTED + " >= ?",
                        new String[]{Long.toString(firstInServerFeed.timeMsInserted),
                                Long.toString(lastInServerFeed.timeMsInserted)}
                );
            } else {
                Log.d(TAG, "updateFeedPostTable for load-more, delete posts from table older than "
                        + firstInServerFeed.timeMsInserted);
                // Delete posts order than firstInServerFeed.timeMsInserted in feed_post table.
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        DatabaseContract.FeedEntry.COLUMN_TIME_INSERTED + " <= ? ",
                        new String[]{Long.toString(firstInServerFeed.timeMsInserted)}
                );
            }

            // Insert feedFromServer into feed_post table
            insertIntoFeedPostTable(db, feedFromServer);

            db.setTransactionSuccessful();
            Log.d(TAG, "updateFeedPostTable succeeded");
        } catch (Exception ex) {
            Log.e(TAG, "Exception in updateFeedPostTable", ex);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Insert feed into feed_post table
     * @param db
     * @param feedPosts
     */
    public void insertIntoFeedPostTable(SQLiteDatabase db, ImmutableList<FeedPost> feedPosts) {
        Log.d(TAG, "In insertIntoFeedPostTable");
        String sql =
                "INSERT INTO " + DatabaseContract.FeedEntry.TABLE_NAME +
                        " (" + DatabaseContract.FEED_COLUMNS_STRING + ") " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement insert = db.compileStatement(sql);

        for (FeedPost post: feedPosts) {
            insert.bindString(1, post.postId);
            insert.bindLong(2, post.timeMsInserted);
            insert.bindString(3, post.userId);
            if (post.postType == null) {
                insert.bindNull(4);
            } else {
                insert.bindString(4, post.postType.toString());
            }
            if (post.category == null) {
                insert.bindNull(5);
            } else {
                insert.bindString(5, post.category);
            }
            if (post.message == null) {
                insert.bindNull(6);
            } else {
                insert.bindString(6, post.message);
            }
            if (post.pictureUrl == null) {
                insert.bindNull(7);
            } else {
                insert.bindString(7, post.pictureUrl);
            }
            if (post.externalLinkUrl == null) {
                insert.bindNull(8);
            } else {
                insert.bindString(8, post.externalLinkUrl);
            }
            if (post.externalLinkImageUrl == null) {
                insert.bindNull(9);
            } else {
                insert.bindString(9, post.externalLinkImageUrl);
            }
            if (post.externalLinkCaption == null) {
                insert.bindNull(10);
            } else {
                insert.bindString(10, post.externalLinkCaption);
            }
            if (post.externalLinkSummary == null) {
                insert.bindNull(11);
            } else {
                insert.bindString(11, post.externalLinkSummary);
            }
            if (post.productBarCode == null) {
                insert.bindNull(12);
            } else {
                insert.bindString(12, post.productBarCode);
            }
            if (post.publicity == null) {
                insert.bindNull(13);
            } else {
                insert.bindString(13, post.publicity.toString());
            }
            insert.bindLong(14, post.likes);
            insert.bindLong(14, post.likes);
            insert.bindLong(15, post.comments);
            insert.bindLong(16, post.isAnonymous ? 1 : 0);
            if (post.postStatus == null) {
                insert.bindNull(17);
            } else {
                insert.bindString(17, post.postStatus.toString());
            }
            insert.bindLong(18, post.timeMsCreated);
            insert.bindLong(19, post.timeMsEdited);
            insert.bindLong(20, post.timeMsChangeToSurface);
            insert.execute();
        }
    }

    /**
     * @param postId feedPost.postId
     * @param timeMsInserted, feedPost.timeMsInserted
     * @return whether feed_post table contains a post with postId and timeMsInserted
     */
    public boolean feedPostTableContains(String postId, long timeMsInserted) {
        String whereClause =
                DatabaseContract.PostEntry.COLUMN_POST_ID + "=? AND " +
                        DatabaseContract.FeedEntry.COLUMN_TIME_INSERTED + "=?";
        Cursor c = DbHelper.getDb().query(
                DatabaseContract.FeedEntry.TABLE_NAME,
                new String[]{DatabaseContract.PostEntry.COLUMN_POST_ID},
                whereClause,
                new String[]{postId, Long.toString(timeMsInserted)},
                null,
                null,
                null
        );

        if (c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Increase comment count of a post in feed_post table.
     * @param postId the post id
     */
    public void incrementCommentCount(String postId) {
        Log.d(TAG, "In incrementCommentCount for " + postId);

        try {
            String sql = "UPDATE " + DatabaseContract.FeedEntry.TABLE_NAME + " " +
                    "SET " + DatabaseContract.PostEntry.COLUMN_COMMENTS + " = " +
                    DatabaseContract.PostEntry.COLUMN_COMMENTS + " + 1 " +
                    "WHERE " + DatabaseContract.PostEntry.COLUMN_POST_ID + " = ? ";

            DbHelper.getDb().execSQL(sql, new String[]{postId});
        } catch (Throwable t) {
            Log.e(TAG, "Exception in incrementCommentCount", t);
        }
    }

    /**
     * Increase like count of a post in feed_post table.
     * @param postId the post id
     */
    public void incrementLikeCount(String postId) {
        Log.d(TAG, "In incrementCommentCount for " + postId);

        try {
            String sql = "UPDATE " + DatabaseContract.FeedEntry.TABLE_NAME + " " +
                    "SET " + DatabaseContract.PostEntry.COLUMN_LIKES + " = " +
                    DatabaseContract.PostEntry.COLUMN_LIKES + " + 1 " +
                    "WHERE " + DatabaseContract.PostEntry.COLUMN_POST_ID + " = ? ";

            DbHelper.getDb().execSQL(sql, new String[]{postId});
        } catch (Throwable t) {
            Log.e(TAG, "Exception in incrementCommentCount", t);
        }
    }

    public static PostDbHandler getInstance() {
        return INSTANCE;
    }
}
