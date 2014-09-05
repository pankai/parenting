package com.palmcel.parenting.login;

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
}
