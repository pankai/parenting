package com.palmcel.parenting.common;


import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utils class
 */
public class Utils {

    private static final String TAG = "Utils";

    /**
     * @param url
     * @return domain from the url
     */
    @Nullable
    public static String getDomainName(String url) {
        URL uri = null;
        try {
            uri = new URL(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid URL, " + url, e);
            return null;
        }
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
