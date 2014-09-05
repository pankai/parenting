package com.palmcel.parenting.post;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.db.DbHelper;
import com.palmcel.parenting.model.Post;

import static com.palmcel.parenting.db.DatabaseContract.PostEntry;

/**
 * Handler that saves post into db and send post to server
 */
public class PostHandler {

    /**
     * Save post to db on a worker thread
     * @param post
     * @return the listenableFuture for the operation
     */
    public ListenableFuture savePostToDbOnThread(final Post post) {
        return ExecutorUtil.execute(new Runnable() {

            @Override
            public void run() {
                savePostToDb(post);
            }
        });
    }

    /**
     * Save a post to database
     * @param post
     */
    private void savePostToDb(Post post) {
        SQLiteDatabase db = DbHelper.getDb();

        ContentValues values = new ContentValues();
        values.put(PostEntry.COLUMN_POST_ID, post.postId);
        values.put(PostEntry.COLUMN_USER_ID, post.userId);
        values.put(PostEntry.COLUMN_POST_TYPE, post.postType.toString());
        values.put(PostEntry.COLUMN_POST_CATEGORY, post.category);
        values.put(PostEntry.COLUMN_MESSAGE, post.message);
        values.put(PostEntry.COLUMN_PICTURE_URL, post.pictureUrl);
        values.put(PostEntry.COLUMN_EXT_LINK_URL, post.externalLinkUrl);
        values.put(PostEntry.COLUMN_EXT_LINK_CAPTION, post.externalLinkCaption);
        values.put(PostEntry.COLUMN_EXT_LINK_SUMMARY, post.externalLinkSummary);
        values.put(PostEntry.COLUMN_PRODUCT_BAR_CODE, post.productBarCode);
        values.put(PostEntry.COLUMN_PUBLICITY, post.publicity.toString());
        values.put(PostEntry.COLUMN_LIKES, post.likes);
        values.put(PostEntry.COLUMN_COMMENTS, post.comments);
        values.put(PostEntry.COLUMN_IS_ANONYMOUS, post.isAnonymous);
        values.put(PostEntry.COLUMN_STATUS, post.postStatus.toString());
        values.put(PostEntry.COLUMN_TIME_CREATED, post.timeMsCreated);
        values.put(PostEntry.COLUMN_TIME_EDITED, post.timeMsEdited);
        values.put(PostEntry.COLUMN_TIME_COMMENTED, post.timeMsCommented);
        values.put(PostEntry.COLUMN_TIME_LASTUPDATED, post.timeMsLastUpdated);

        db.replaceOrThrow(PostEntry.TABLE_NAME, "", values);
    }
}
