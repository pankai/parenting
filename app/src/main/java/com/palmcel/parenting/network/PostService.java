package com.palmcel.parenting.network;

import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.Post;
import com.palmcel.parenting.model.PostComment;
import com.palmcel.parenting.model.PostLike;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * REST APIs related to post.
 */
public interface PostService {
    /**
     * Save a post into server.
     * @param post
     * @return
     */
    @POST("/api/v1/post")
    Response newPost(@Header("Authorization") String authorization, @Body Post post);

    /**
     * Load feed from server
     * @param userId
     * @param timeMsInsertedSince
     * @param maxToFetch
     * @param largestInsertTimeAtClient
     * @return feed posts from server sorted by timeMsInserted in DESC order.
     */
    @GET("/api/v1/feed")
    List<FeedPost> getFeed(
            @Query("user_id") String userId,
            @Query("time_ms_inserted_since") long timeMsInsertedSince,
            @Query("max_to_fetch") int maxToFetch,
            @Query("largest_insert_time_at_client") long largestInsertTimeAtClient);

    /**
     * Save a post comment to server.
     * @param postComment a post comment
     * @return
     */
    @POST("/api/v1/post/comments")
    Response newComment(
            @Header("Authorization") String authorization,
            @Body PostComment postComment);

    /**
     * Load post comments from server
     * @param postId
     * @param timeMsCreatedSince
     * @param maxToFetch
     * @return post comments from server sorted by timeMsCreated in DESC order.
     */
    @GET("/api/v1/post/comments")
    List<PostComment> getPostComments(
            @Query("post_id") String postId,
            @Query("time_ms_created_since") long timeMsCreatedSince,
            @Query("max_to_fetch") int maxToFetch);

    /**
     * Like or unlike a post and send the like change to server.
     * @param postLike a post like
     * @return
     */
    @POST("/api/v1/post/likes")
    Response likePost(
            @Header("Authorization") String authorization,
            @Body PostLike postLike);

    /**
     * Load post likes from server
     * @param postId
     * @param timeMsLikeSince
     * @param maxToFetch
     * @return post likes from server sorted by timeMsLike in DESC order.
     */
    @GET("/api/v1/post/likes")
    List<PostLike> getPostLikes(
            @Query("post_id") String postId,
            @Query("time_ms_like_since") long timeMsLikeSince,
            @Query("max_to_fetch") int maxToFetch);
}
