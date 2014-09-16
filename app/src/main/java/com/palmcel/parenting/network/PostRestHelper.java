package com.palmcel.parenting.network;

import com.palmcel.parenting.common.Log;

import retrofit.RestAdapter;

/**
 * Singleton Retrofit REST adapter for posts.
 */
public class PostRestHelper {
    private static PostService sPostService;
    private static final String ENDPOINT_FROM_GENYMOTION = "http://10.0.3.2:3000";

    public static synchronized PostService getPostService() {
        if (sPostService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(
                            Log.LOGGING_DEBUG ? RestAdapter.LogLevel.FULL :
                                    RestAdapter.LogLevel.NONE)
                    .setEndpoint(ENDPOINT_FROM_GENYMOTION)
                    .build();

            sPostService = restAdapter.create(PostService.class);
        }

        return sPostService;
    }
}
