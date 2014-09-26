package com.palmcel.parenting.feed;

import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.login.LoggedInUser;
import com.palmcel.parenting.model.PostLike;
import com.palmcel.parenting.network.PostRestHelper;

import java.util.concurrent.Callable;

/**
 * Send post like/unlike to server
 */
public class LikeHandler {

    /**
     * Save like/unlike to sever on a worker thread
     * @param postLike
     * @return the listenableFuture for the operation
     */
    public ListenableFuture saveLikeChangeToServerOnThread(final PostLike postLike) {
        return ExecutorUtil.execute(new Callable<PostLike>() {

            @Override
            public PostLike call() {
                saveLikeChangeToServer(postLike);
                return postLike;
            }
        });
    }

    /**
     * Send like change to server
     * @param postLike
     */
    private void saveLikeChangeToServer(PostLike postLike) {
        PostRestHelper.getPostService().likePost(
                LoggedInUser.genUserAuthentication(),
                postLike);
    }
}
