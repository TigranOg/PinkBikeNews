package com.rss.pinkbike.util;

import android.app.Application;

/**
 * Created with IntelliJ IDEA.
 * User: Tiga
 * Date: 5/18/13
 * Time: 11:16 PM
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initSingleton();
    }

    protected void initSingleton() {
        ApplicationManager.initInstance();
    }

}
