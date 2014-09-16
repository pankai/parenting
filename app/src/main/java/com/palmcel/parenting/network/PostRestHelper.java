package com.palmcel.parenting.network;

import com.palmcel.parenting.common.Log;

import retrofit.RestAdapter;

/**
 * Singleton Retrofit REST adapter for posts.
 */
public class PostRestHelper {
    private static PostService sPostService;

    public static synchronized PostService getPostService() {
        if (sPostService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(
                            Log.LOGGING_DEBUG ? RestAdapter.LogLevel.FULL :
                                    RestAdapter.LogLevel.NONE)
                    .setEndpoint("http://localhost")
                    .build();

            sPostService = restAdapter.create(PostService.class);
        }

        return sPostService;
    }
}
