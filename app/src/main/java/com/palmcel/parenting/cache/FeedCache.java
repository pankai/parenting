package com.palmcel.parenting.cache;

import android.database.sqlite.SQLiteException;
import android.text.format.DateUtils;

import com.google.common.collect.ImmutableList;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.common.Log;
import com.palmcel.parenting.db.DatabaseContract;
import com.palmcel.parenting.db.DbHelper;
import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.FeedPostBuilder;

/**
 * Singleton that caches the feed
 */
public class FeedCache {

    private static final String TAG = "FeedCache";
    private static final FeedCache INSTANCE = new FeedCache();
    private static final long sStaleIntervalMs = 3 * DateUtils.MINUTE_IN_MILLIS;

    private ImmutableList<FeedPost> mCachedFeed;
    private long mLastUpdatedMs;

    private FeedCache() {
        mCachedFeed = ImmutableList.of();
    }

    public static FeedCache getInstance() {
        return INSTANCE;
    }

    public boolean isUpToDate() {
        return System.currentTimeMillis() - mLastUpdatedMs < sStaleIntervalMs;
    }

    public synchronized ImmutableList<FeedPost> getCachedFeed() {
        return mCachedFeed;
    }

    public synchronized boolean isEmpty() {
        return mCachedFeed.isEmpty();
    }

    /**
     * Update memory cache with data from database.
     * Merge mCachedFeed and dbFeedPost into mCacheFeed.
     * There should be no hole in the merge results.
     * @param dbFeedPosts data from db
     * @return updated feed from cache
     */
    public synchronized ImmutableList<FeedPost> updateCacheFromDb(
            ImmutableList<FeedPost> dbFeedPosts) {
        Log.d(TAG, "In updateCacheFromDb, mCachedFeed=" + mCachedFeed.size() +
                ", dbFeedPosts=" + dbFeedPosts.size());
        if (mCachedFeed.isEmpty()) {
            mCachedFeed = dbFeedPosts;
            mLastUpdatedMs = System.currentTimeMillis(); // TODO: should get the time from dbFeedPosts
            return mCachedFeed;
        }
        if (dbFeedPosts.isEmpty()) {
            return mCachedFeed;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(mCachedFeed);

        FeedPost lastInMemCache = mCachedFeed.get(mCachedFeed.size() - 1);

        boolean hasFoundLast = false;

        for (FeedPost post: dbFeedPosts) {
            if (hasFoundLast) {
                builder.add(post);
            } else if (post.timeToSort == lastInMemCache.timeToSort) {
                hasFoundLast = true;
            } else if (post.timeToSort < lastInMemCache.timeToSort) {
                // There is hole between memory cache and dbFeedPosts.
                Log.w(TAG, "Warning, there is a hole between memory cache and dbFeedPosts.");
                clearFeedPostTableOnThread();
                return mCachedFeed;
            }
        }

        mLastUpdatedMs = System.currentTimeMillis();  // TODO: should get the time from dbFeedPosts
        mCachedFeed = builder.build();

        return mCachedFeed;
    }

    /**
     * @return the largest sort time of post in mCachedFeed or 0 if mCachedFeed is empty.
     */
    public synchronized long getLargestSortTime() {
        if (mCachedFeed.isEmpty()) {
            return 0;
        }

        return mCachedFeed.get(0).timeToSort;
    }

    /**
     * Clear all rows in feed_post table on a worker thread
     */
    private void clearFeedPostTableOnThread() {
        Log.d(TAG, "In clearFeedPostTableOnThread");
        ExecutorUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DbHelper.getDb().delete(DatabaseContract.FeedEntry.TABLE_NAME, null, null);
                } catch (SQLiteException ex) {
                    Log.e(TAG, "clearFeedPostTable failed.", ex);
                }
            }
        });
    }

    /**
     * Update mCachedFeed with the latest feed loaded from server
     * @param feedFromServer feed from server. The feed starts from the latest post in the feed. It
     *                       is not middle portion in the feed.
     * @return updated feed from cache
     */
    public synchronized ImmutableList<FeedPost> updateCacheFromServer(
            ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateCacheFromServer, mCachedFeed=" + mCachedFeed.size() +
                ", feedFromServer=" + feedFromServer.size());
        if (mCachedFeed.isEmpty()) {
            mCachedFeed = feedFromServer;
            mLastUpdatedMs = System.currentTimeMillis();
            return mCachedFeed;
        }
        if (feedFromServer.isEmpty()) {
            Log.d(TAG, "updateCacheFromServer, empty feed from server");
            mCachedFeed = ImmutableList.of();
            return mCachedFeed;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(feedFromServer);

        FeedPost lastInServerFeed = feedFromServer.get(feedFromServer.size() - 1);

        boolean hasFoundLast = false;

        for (FeedPost post: mCachedFeed) {
            if (hasFoundLast) {
                builder.add(post);
            } else if (post.timeToSort == lastInServerFeed.timeToSort) {
                hasFoundLast = true;
            } else if (post.timeToSort < lastInServerFeed.timeToSort) {
                // There is hole between memory cache and feedFromServer.
                Log.w(TAG, "Warning, there is a hole between memory cache and feedFromServer.");
                mLastUpdatedMs = System.currentTimeMillis();
                mCachedFeed = feedFromServer;
                return mCachedFeed;
            }
        }

        mLastUpdatedMs = System.currentTimeMillis();
        mCachedFeed = builder.build();

        return mCachedFeed;
    }

    /**
     * Update mCachedFeed with the load-more feed from server
     * TODO: timeToSort should be unique in a feed. We need to make sure it. Same for comments
     * of a post.
     * @param timeSince, the smallest sort time of the feed at client before
     *                             loading more
     * @param feedFromServer feed from server. The feed is a result of load more. That is, it
     *                        doesn't start with the latest post in the feed at server.
     * @return updated feed from cache
     */
    public synchronized ImmutableList<FeedPost> updateCacheFromServer(
            long timeSince,
            ImmutableList<FeedPost> feedFromServer) {
        Log.d(TAG, "In updateCacheFromServer for load-more, mCachedFeed=" + mCachedFeed.size() +
                ", feedFromServer=" + feedFromServer.size() +
                ", timeSince=" + timeSince);
        if (mCachedFeed.isEmpty()) {
            mCachedFeed = feedFromServer;
            Log.e(TAG, "mCachedFeed became empty after loading more", new RuntimeException());
            return mCachedFeed;
        }
        if (feedFromServer.isEmpty()) {
            Log.d(TAG, "updateCacheFromServer for load-more, empty feed from server");
            return mCachedFeed;
        }

        FeedPost lastInMemory = mCachedFeed.get(mCachedFeed.size() - 1);
        FeedPost firstFromServer = feedFromServer.get(0);

        if (timeSince != firstFromServer.timeToSort) {
            Log.e(TAG, "Inconsistent timeSince after loading more, " +
                    timeSince + " vs " +
                    firstFromServer.timeToSort, new RuntimeException());
            return mCachedFeed;
        }

        if (lastInMemory.timeToSort != firstFromServer.timeToSort) {
            Log.e(TAG, "Unmatched timeSince after loading more, " +
                    lastInMemory.timeToSort + " vs " +
                    firstFromServer.timeToSort, new RuntimeException());
            return mCachedFeed;
        }

        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        builder.addAll(mCachedFeed.subList(0, mCachedFeed.size() - 1));
        builder.addAll(feedFromServer);

        mCachedFeed = builder.build();

        return mCachedFeed;
    }

    /**
     * Increase comment count of a post.
     * @param postId the post id
     */
    public synchronized void incrementCommentCount(String postId) {
        Log.d(TAG, "In incrementCommentCount for " + postId);
        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        for (FeedPost post: mCachedFeed) {
            if (!post.postId.equals(postId)) {
                builder.add(post);
            } else {
                FeedPostBuilder feedPostBuilder = new FeedPostBuilder().from(post);
                feedPostBuilder.setComments(post.comments + 1);
                builder.add(feedPostBuilder.build());
            }
        }

        mCachedFeed = builder.build();
    }

    /**
     * Increase or decrease like count of a post and isLiked.
     * @param postId the post id
     */
    public synchronized void changeLikeCountAndIsLiked(String postId, boolean isLiked) {
        Log.d(TAG, "In changeLikeCount for " + postId + ", isLiked=" + isLiked);
        ImmutableList.Builder<FeedPost> builder = ImmutableList.builder();
        for (FeedPost post: mCachedFeed) {
            if (!post.postId.equals(postId)) {
                builder.add(post);
            } else {
                FeedPostBuilder feedPostBuilder = new FeedPostBuilder().from(post);
                feedPostBuilder.setLikes(post.likes + (isLiked ? 1 : -1));
                feedPostBuilder.setIsLiked(isLiked);
                builder.add(feedPostBuilder.build());
            }
        }

        mCachedFeed = builder.build();
    }
}
