package com.palmcel.parenting.application;

import android.app.Application;

import com.palmcel.parenting.common.AppContext;

/**
 * Custom application class
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppContext.setApplicationContext(getApplicationContext());
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/roboto_regular.ttf");
    }
}
