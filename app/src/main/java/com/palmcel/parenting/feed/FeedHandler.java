package com.palmcel.parenting.feed;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.network.PostRestHelper;

import java.util.List;

/**
 * Handler that load feed from server
 */
public class FeedHandler {

    private static final String TAG = "FeedHandler";

    /**
     * Save post to saver on a worker thread
     * @param userId feed of this user
     * @param maxToFetch max number of posts to fetch
     * @param largestInsertTimeAtClient the current largest insert time of feed post in client
     *                                  cache
     * @return the listenableFuture for the operation
     */
    public ListenableFuture getFeedFromServerOnThread(
            final String userId,
            final long timeMsInsertedSince,
            final int maxToFetch,
            final long largestInsertTimeAtClient) {
        return ExecutorUtil.execute(new Runnable() {

            @Override
            public void run() {
                getFeedPostFromServer(
                        userId, timeMsInsertedSince, maxToFetch, largestInsertTimeAtClient);
            }
        });
    }

    /**
     * Load the latest feed from the server
     * @return list of feed posts sorted by timeMsInserted in DESC order.
     */
    public ImmutableList<FeedPost> getFeedPostFromServer(
            String userId,
            long timeMsInsertedSince,
            int maxToFetch,
            long largestInsertTimeAtClient) {
        Log.d(TAG, "In getFeedPostFromServer");
        List<FeedPost> feed = PostRestHelper.getPostService().getFeed(
                userId,
                timeMsInsertedSince,
                maxToFetch,
                largestInsertTimeAtClient);

        return feed == null ? ImmutableList.<FeedPost>of() : ImmutableList.copyOf(feed);
    }
}
