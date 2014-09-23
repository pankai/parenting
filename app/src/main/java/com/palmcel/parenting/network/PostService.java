package com.palmcel.parenting.network;

import com.palmcel.parenting.model.FeedPost;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
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
    Response newPost(@Body NewPostRequest post);

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
}
