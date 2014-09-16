package com.palmcel.parenting.network;

import com.palmcel.parenting.model.Post;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * REST APIs related to post.
 */
public interface PostService {
    @POST("/posts/new")
    Response newPost(@Body Post post);
}
