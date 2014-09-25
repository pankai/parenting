package com.palmcel.parenting.post;

import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.login.LoggedInUser;
import com.palmcel.parenting.model.Post;
import com.palmcel.parenting.network.PostRestHelper;

/**
 * Handler that saves post into db and send post to server
 */
public class PostHandler {

    /**
     * Save post to saver on a worker thread
     * @param post
     * @return the listenableFuture for the operation
     */
    public ListenableFuture savePostToServerOnThread(final Post post) {
        return ExecutorUtil.execute(new Runnable() {

            @Override
            public void run() {
                savePostToServer(post);
            }
        });
    }

    /**
     * Save the newly a post to server
     * @param post
     */
    private void savePostToServer(Post post) {
        PostRestHelper.getPostService().newPost(
                LoggedInUser.genUserAuthentication(),
                post);
    }
}
