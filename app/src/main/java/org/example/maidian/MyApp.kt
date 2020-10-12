package org.example.maidian

import android.app.Application

import org.gioneco.analytics.android.sdk.BuildConfig
import org.gioneco.analytics.android.sdk.http.DataAPI


/**
 * Created by zsq
 * on 2020-09-29
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DataAPI.init(this)
    }
}
