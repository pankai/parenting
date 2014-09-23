package com.palmcel.parenting.network;

import com.palmcel.parenting.model.Post;

/**
 * HTTP post body for new-post request.
 */
public class NewPostRequest {
    public Post post;
    public String username;
    public String password;

    public NewPostRequest(Post post, String username, String password) {
        this.post = post;
        this.username = username;
        this.password = password;
    }

    public static NewPostRequest genAddPostRequest(Post post) {
        return new NewPostRequest(post, "pkdebug", "pkdebug"); // TODO
    }
}
