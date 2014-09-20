package com.palmcel.parenting.network;

import com.palmcel.parenting.model.FeedPost;
import com.palmcel.parenting.model.Post;

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
    @POST("/api/v1/post")
    Response newPost(@Body Post post);

    @GET("/api/v1/feed")
    List<FeedPost> getFeed(
            @Query("user_id") String userId,
            @Query("max_to_fetch") int maxToFetch,
            @Query("largest_insert_time_at_client") long largestInsertTimeAtClient);
}
