package com.lfp.lfp_databind_master;

import android.app.Application;
import android.content.Context;

/**
 * lfp
 * 2018/4/27
 * 程序入口
 */
public class LfpDatabingApp extends Application {

    public static LfpDatabingApp app;

    public static Context getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
