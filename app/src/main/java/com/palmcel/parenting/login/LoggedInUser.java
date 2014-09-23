package com.palmcel.parenting.login;

import android.util.Base64;

/**
 * Singleton class that store the logged-in user id
 */
public class LoggedInUser {
    private static String sLoggedInUserId = "pkdebug";

    private LoggedInUser() {}

    public static void setLoggedInUserId(String loggedInUserId) {
        sLoggedInUserId =loggedInUserId;
    }

    public static String getLoggedInUserId() {
        return sLoggedInUserId;
    }

    /**
     * Generate basic authentication header string.
     * @return e.g. Basic cGtkZWJ1Zzpwa2RlYnVn
     */
    public static String genUserAuthentication() {
        String source = sLoggedInUserId + ":" + sLoggedInUserId; // TODO
        return "Basic " +
                Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

    }
}
