package com.palmcel.parenting.comment;

import com.google.common.util.concurrent.ListenableFuture;
import com.palmcel.parenting.common.ExecutorUtil;
import com.palmcel.parenting.login.LoggedInUser;
import com.palmcel.parenting.model.PostComment;
import com.palmcel.parenting.network.PostRestHelper;

import java.util.concurrent.Callable;

/**
 * Handler that saves post comment to server
 */
public class CommentHandler {

    /**
     * Save post comment to saver on a worker thread
     * @param postComment
     * @return the listenableFuture for the operation
     */
    public ListenableFuture<PostComment> saveCommentToServerOnThread(
            final PostComment postComment) {
        return ExecutorUtil.execute(new Callable<PostComment>() {

            @Override
            public PostComment call() {
                saveCommentToServer(postComment);
                return postComment;
            }
        });
    }

    /**
     * Save the newly-created comment to server
     * @param postComment post comment
     */
    private void saveCommentToServer(PostComment postComment) {
        PostRestHelper.getPostService().newComment(
                LoggedInUser.genUserAuthentication(),
                postComment);
    }
}
