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
            if (feedPostTableContains(lastInServerFeed.postId, lastInServerFeed.timeToSort)) {
                Log.d(TAG, "updateFeedPostTable, feed_post table contains post (" +
                        lastInServerFeed.postId + ", " + lastInServerFeed.timeToSort + ")");

                // Delete posts before lastInServerFeed.timeToSort in feed_post table
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        DatabaseContract.FeedEntry.COLUMN_TIME_SORT + " >= ?",
                        new String[]{Long.toString(lastInServerFeed.timeToSort)}
                );
            } else {
                Log.d(TAG, "updateFeedPostTable, feed_post table doesn't contain post (" +
                        lastInServerFeed.postId + ", " + lastInServerFeed.timeToSort + ")");
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
     * @param timeSince the load-more feed is loaded from timeSince and older
     *                            than it.
     * @param feedFromServer feed from server sorted by timeToSort in DESC order.
     *                       The feed is a result of load more. That is, it
     *                       doesn't start with the latest post in the feed at server.
     */
    public void updateFeedPostTable(
            long timeSince,
            ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateFeedPostTable for load-more, timeSince=" +
                timeSince + ", feedFromServer=" + feedFromServer.size());

        if (feedFromServer.isEmpty()) {
            Log.d(TAG, "updateFeedPostTable for load-more, empty feed from server");
            return;
        }

        FeedPost firstInServerFeed = feedFromServer.get(0);

        if (!feedPostTableContains(firstInServerFeed.postId, firstInServerFeed.timeToSort)) {
            Log.d(TAG, "updateFeedPostTable for load-more, feed_post table doesn't contain " +
                    firstInServerFeed.timeToSort);
            return;
        }

        FeedPost lastInServerFeed = feedFromServer.get(feedFromServer.size() - 1);

        SQLiteDatabase db = DbHelper.getDb();
        db.beginTransaction();

        try {
            // Check whether feed_post table contains lastInServerFeed
            if (feedPostTableContains(lastInServerFeed.postId, lastInServerFeed.timeToSort)) {
                Log.d(TAG, "updateFeedPostTable for load-more, delete posts from table between " +
                        firstInServerFeed.timeToSort +
                        " and " + lastInServerFeed.timeToSort);

                // Delete posts between firstInServerFeed.timeToSort and
                // lastInServerFeed.timeToSort in feed_post table
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        DatabaseContract.FeedEntry.COLUMN_TIME_SORT + " <= ? AND " +
                                DatabaseContract.FeedEntry.COLUMN_TIME_SORT + " >= ?",
                        new String[]{Long.toString(firstInServerFeed.timeToSort),
                                Long.toString(lastInServerFeed.timeToSort)}
                );
            } else {
                Log.d(TAG, "updateFeedPostTable for load-more, delete posts from table older than "
                        + firstInServerFeed.timeToSort);
                // Delete posts order than firstInServerFeed.timeToSort in feed_post table.
                db.delete(
                        DatabaseContract.FeedEntry.TABLE_NAME,
                        DatabaseContract.FeedEntry.COLUMN_TIME_SORT + " <= ? ",
                        new String[]{Long.toString(firstInServerFeed.timeToSort)}
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
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement insert = db.compileStatement(sql);

        for (FeedPost post: feedPosts) {
            insert.bindString(1, post.postId);
            insert.bindLong(2, post.timeToSort);
            insert.bindLong(3, post.isLiked ? 1 : 0);
            insert.bindString(4, post.userId);
            if (post.postType == null) {
                insert.bindNull(5);
            } else {
                insert.bindString(5, post.postType.toString());
            }
            if (post.category == null) {
                insert.bindNull(6);
            } else {
                insert.bindString(6, post.category);
            }
            if (post.message == null) {
                insert.bindNull(7);
            } else {
                insert.bindString(7, post.message);
            }
            if (post.pictureUrl == null) {
                insert.bindNull(8);
            } else {
                insert.bindString(8, post.pictureUrl);
            }
            if (post.externalLinkUrl == null) {
                insert.bindNull(9);
            } else {
                insert.bindString(9, post.externalLinkUrl);
            }
            if (post.externalLinkImageUrl == null) {
                insert.bindNull(10);
            } else {
                insert.bindString(10, post.externalLinkImageUrl);
            }
            if (post.externalLinkCaption == null) {
                insert.bindNull(11);
            } else {
                insert.bindString(11, post.externalLinkCaption);
            }
            if (post.externalLinkSummary == null) {
                insert.bindNull(12);
            } else {
                insert.bindString(12, post.externalLinkSummary);
            }
            if (post.productBarCode == null) {
                insert.bindNull(13);
            } else {
                insert.bindString(13, post.productBarCode);
            }
            if (post.publicity == null) {
                insert.bindNull(14);
            } else {
                insert.bindString(14, post.publicity.toString());
            }
            insert.bindLong(15, post.likes);
            insert.bindLong(16, post.comments);
            insert.bindLong(17, post.isAnonymous ? 1 : 0);
            if (post.postStatus == null) {
                insert.bindNull(18);
            } else {
                insert.bindString(18, post.postStatus.toString());
            }
            insert.bindLong(19, post.timeMsCreated);
            insert.bindLong(20, post.timeMsEdited);
            insert.bindLong(21, post.timeMsChangeToSurface);
            insert.execute();
        }
    }

    /**
     * @param postId feedPost.postId
     * @param timeToSort, feedPost.timeToSort
     * @return whether feed_post table contains a post with postId and timeToSort
     */
    public boolean feedPostTableContains(String postId, long timeToSort) {
        String whereClause =
                DatabaseContract.PostEntry.COLUMN_POST_ID + "=? AND " +
                        DatabaseContract.FeedEntry.COLUMN_TIME_SORT + "=?";
        Cursor c = DbHelper.getDb().query(
                DatabaseContract.FeedEntry.TABLE_NAME,
                new String[]{DatabaseContract.PostEntry.COLUMN_POST_ID},
                whereClause,
                new String[]{postId, Long.toString(timeToSort)},
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
     * Increase or decrease like count of a post and isLiked in feed_post table.
     * @param postId the post id
     */
    public void changeLikeCountAndIsLiked(String postId, boolean isLiked) {
        Log.d(TAG, "In changeLikeCount for " + postId + ", isLiked=" + isLiked);

        String delta = isLiked ? " + 1 " : " - 1 ";
        try {
            String sql = "UPDATE " + DatabaseContract.FeedEntry.TABLE_NAME + " " +
                    "SET " + DatabaseContract.PostEntry.COLUMN_LIKES + " = " +
                    DatabaseContract.PostEntry.COLUMN_LIKES + delta + ", " +
                    DatabaseContract.FeedEntry.COLUMN_IS_LIKED + " = " +
                    (isLiked ? 1 : 0) + " " +
                    "WHERE " + DatabaseContract.PostEntry.COLUMN_POST_ID + " = ? ";

            DbHelper.getDb().execSQL(sql, new String[]{postId});
        } catch (Throwable t) {
            Log.e(TAG, "Exception in changeLikeCount", t);
        }
    }

    public static PostDbHandler getInstance() {
        return INSTANCE;
    }
}
