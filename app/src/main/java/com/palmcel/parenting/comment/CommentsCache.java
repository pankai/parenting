package com.palmcel.parenting.comment;

import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.LruCache;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.PostComment;

/**
 * Singleton class for caching post comments
 */
public class CommentsCache {

    private static final String TAG = "CommentsCache";
    private static final long STALE_THRESHOLD = DateUtils.MINUTE_IN_MILLIS;

    private static final int MAX_CACHE_SIZE = 100;
    private static CommentsCache INSTANCE = new CommentsCache();

    private static class CommentsWithCreateTime {
        private ImmutableList<PostComment> postComments;
        // The time postComments is created from server data.
        private long createdTime;

        private CommentsWithCreateTime(ImmutableList<PostComment> comments) {
            this.postComments = comments;
            this.createdTime = System.currentTimeMillis();
        }

        private CommentsWithCreateTime(ImmutableList<PostComment> comments, long createdTime) {
            this.postComments = comments;
            this.createdTime = createdTime;
        }
    }

    // Map<post id, List of comments sorted by timeMsCreated in DESC order>
    private LruCache<String, CommentsWithCreateTime> mCommentsCache;

    private CommentsCache() {
        mCommentsCache = new LruCache<String, CommentsWithCreateTime>(MAX_CACHE_SIZE);
    }

    public synchronized void putPostComments(
            String postId,
            ImmutableList<PostComment> postComments) {
        mCommentsCache.put(postId, new CommentsWithCreateTime(postComments));
    }

    /**
     * @param postId post id
     * @return If the cached comments for postId exists and is up-to-date, return it.
     * Otherwise return null.
     */
    @Nullable
    public synchronized ImmutableList<PostComment> getCommentsIfUpToDate(String postId) {
        CommentsWithCreateTime commentsWithCreateTime = mCommentsCache.get(postId);

        if (commentsWithCreateTime == null ||
                System.currentTimeMillis() - commentsWithCreateTime.createdTime > STALE_THRESHOLD) {
            return null;
        } else {
            return commentsWithCreateTime.postComments;
        }
    }

    /**
     * Update mCommentsCache with the latest comments loaded from server
     * @param postId the post id
     * @param commentsFromServer post comments from server. The comments starts from the latest
     *                           comments. It is not middle portion in the post comments. Comments
     *                           are sorted by timeMsCreated in DESC order in commentsFromServer.
     * @return updated comments from cache for the post
     */
    public synchronized ImmutableList<PostComment> updateCommentsCacheFromServer(
            String postId,
            ImmutableList<PostComment> commentsFromServer) {
        Log.d(TAG, "In updateCommentsCacheFromServer, mCommentsCache=" + mCommentsCache.size() +
                ", commentsFromServer=" + commentsFromServer.size() +
                ", postId=" + postId);

        CommentsWithCreateTime commentsWithTime = mCommentsCache.get(postId);
        ImmutableList<PostComment> postCommentsInCache =
                commentsWithTime == null ? null : commentsWithTime.postComments;

        if (postCommentsInCache == null ||
                postCommentsInCache.isEmpty() ||
                commentsFromServer.isEmpty()) {
            mCommentsCache.put(postId, new CommentsWithCreateTime(commentsFromServer));
            return commentsFromServer;
        }

        ImmutableList.Builder<PostComment> builder = ImmutableList.builder();
        builder.addAll(commentsFromServer);

        PostComment lastInServer = commentsFromServer.get(commentsFromServer.size() - 1);

        boolean hasFoundLast = false;

        for (PostComment comment: postCommentsInCache) {
            if (hasFoundLast) {
                builder.add(comment);
            } else if (comment.timeMsCreated == lastInServer.timeMsCreated) {
                hasFoundLast = true;
            } else if (comment.timeMsCreated < lastInServer.timeMsCreated) {
                // There is hole between memory cache and commentsFromServer.
                Log.w(TAG, "Warning, there is a hole between memory cache and commentsFromServer.");
                mCommentsCache.put(postId, new CommentsWithCreateTime(commentsFromServer));
                return commentsFromServer;
            }
        }

        ImmutableList<PostComment> newComments = builder.build();
        mCommentsCache.put(postId, new CommentsWithCreateTime(newComments));

        return newComments;
    }

    /**
     * Update mCommentsCache with the load-more comments from server
     * @param timeMsCreatedSince, the smallest create time of the comments at client before
     *                             loading more
     * @param commentsFromServer comments from server. The comments are a result of load more.
     *                           That is, they don't start with the latest comment in the post
     *                           at server.
     * @return updated post comments from cache
     */
    public synchronized ImmutableList<PostComment> updateCommentsCacheFromServer(
            String postId,
            long timeMsCreatedSince,
            ImmutableList<PostComment> commentsFromServer) {
        Log.d(TAG, "In updateCommentsCacheFromServer for load-more, mCommentsCache=" +
                mCommentsCache.size() +
                ", commentsFromServer=" + commentsFromServer.size() +
                ", timeMsCreatedSince=" + timeMsCreatedSince +
                ", postId=" + postId);

        CommentsWithCreateTime commentsWithTime = mCommentsCache.get(postId);
        ImmutableList<PostComment> postCommentsInCache =
                commentsWithTime == null ? null : commentsWithTime.postComments;

        if (postCommentsInCache == null || postCommentsInCache.isEmpty()) {
            mCommentsCache.put(postId, new CommentsWithCreateTime(commentsFromServer, 0));
            Log.e(TAG, "mCommentsCache became empty for post, " + postId +
                    " after loading more", new RuntimeException());
            return commentsFromServer;
        }
        if (commentsFromServer.isEmpty()) {
            Log.d(TAG, "updateCommentsCacheFromServer for load-more, 0 comments from server");
            return postCommentsInCache;
        }

        PostComment lastInMemory = postCommentsInCache.get(postCommentsInCache.size() - 1);
        PostComment firstFromServer = commentsFromServer.get(0);

        if (timeMsCreatedSince != firstFromServer.timeMsCreated) {
            Log.e(TAG, "Inconsistent timeMsCreatedSince after loading more comments, " +
                    timeMsCreatedSince + " vs " +
                    firstFromServer.timeMsCreated, new RuntimeException());
            return postCommentsInCache;
        }

        if (lastInMemory.timeMsCreated != firstFromServer.timeMsCreated) {
            Log.e(TAG, "Unmatched timeMsCreatedSince after loading more comments, " +
                    lastInMemory.timeMsCreated + " vs " +
                    firstFromServer.timeMsCreated, new RuntimeException());
            return postCommentsInCache;
        }

        ImmutableList.Builder<PostComment> builder = ImmutableList.builder();
        builder.addAll(postCommentsInCache.subList(0, postCommentsInCache.size() - 1));
        builder.addAll(commentsFromServer);

        ImmutableList<PostComment> newComments = builder.build();
        // Don't change the CommentsWithCreateTime.createdTime because it is load more.
        mCommentsCache.put(
                postId,
                new CommentsWithCreateTime(newComments, commentsWithTime.createdTime));

        return newComments;
    }

    public static CommentsCache getInstance() {
        return INSTANCE;
    }
}
