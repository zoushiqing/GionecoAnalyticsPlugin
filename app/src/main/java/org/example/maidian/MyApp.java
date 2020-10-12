package org.example.maidian;

import android.app.Application;

import org.gioneco.analytics.android.sdk.DataAPI;


/**
 * Created by zsq
 * on 2020-09-29
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initGionecoDataAPI(this);
    }

    /**
     * 初始化埋点 SDK
     *
     * @param application Application
     */
    private void initGionecoDataAPI(Application application) {
        DataAPI.init(application);
    }
}
