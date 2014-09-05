package com.palmcel.parenting.common;

import android.content.Context;

/**
 * Singleton class that save ApplicationContext
 */
public class AppContext {
    public static Context sApplicationContext;

    /**
     * Set application context. This method should be called in Application.onCreate;
     * @param applicationContext
     */
    public static void setApplicationContext(Context applicationContext) {
        sApplicationContext = applicationContext;
    }

    public static Context getAplicationContext() {
        return sApplicationContext;
    }
}
