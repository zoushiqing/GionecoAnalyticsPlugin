package org.gioneco.analytics.android.sdk.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import org.gioneco.analytics.android.sdk.http.DataAPI
import org.json.JSONObject
import java.util.*
import android.os.Build.VERSION.SDK_INT



/**
 *
 * @Description: 记录屏幕浏览事件工具类
 * @Author: Niko
 * @Date: 2020-10-12
 *
 */
class DataAppViewScreenHelper {


    companion object {
        private val mIgnoredActivities = ArrayList<String>()


        /**
         * 记录页面浏览事件
         * @param activity
         */
        @Keep
        fun trackAppViewScreen(activity: Activity) {
            try {
                if (mIgnoredActivities.contains(activity.javaClass.canonicalName)) {
                    return
                }
                val properties = JSONObject()
                properties.put("\$activity", activity.javaClass.canonicalName)
                properties.put("\$title", getActivityTitle(activity));
                DataAPI.getInstance().track("AppViewScreen", properties)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /**
         * 注册Application.registerActivityLifecycleCallbacks
         * @param application
         */
        fun registerActivityLifecycleCallbacks(application: Application) {
            application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityPaused(activity: Activity?) {}

                override fun onActivityResumed(activity: Activity?) {
                    if (activity != null) {
                        trackAppViewScreen(activity)
                    }
                }

                override fun onActivityStarted(activity: Activity?) {}

                override fun onActivityDestroyed(activity: Activity?) {}

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

                override fun onActivityStopped(activity: Activity?) {}

                override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}

            })
        }

        private fun getToolbarTitle(activity: Activity): String {
            val actionBar = activity.actionBar
            if (actionBar != null) {
                if (!TextUtils.isEmpty(actionBar.title)) {
                    return actionBar.title.toString()
                }
            } else {
                if (activity is AppCompatActivity) {
                    val appCompatActivity = activity as AppCompatActivity
                    val supportActionBar = appCompatActivity.supportActionBar
                    if (supportActionBar != null) {
                        if (!TextUtils.isEmpty(supportActionBar.title)) {
                            return supportActionBar.title.toString()
                        }
                    }
                }
            }
            return ""
        }

        /**
         * 获取 Activity 的 title
         */
        @SuppressLint("ObsoleteSdkInt")
        @SuppressWarnings("all")
        private fun getActivityTitle(activity: Activity): String {
            var activityTitle = ""
            try {
                activityTitle = activity.title.toString()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    val toolbarTitle = getToolbarTitle(activity)
                    if (!TextUtils.isEmpty(toolbarTitle)) {
                        activityTitle = toolbarTitle
                    }
                }
                if (TextUtils.isEmpty(activityTitle)) {
                    val packageManager = activity.packageManager
                    if (packageManager != null) {
                        val activityInfo = packageManager.getActivityInfo(activity.componentName, 0)
                        if (activityInfo != null) {
                            activityTitle = activityInfo.loadLabel(packageManager).toString()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return activityTitle
        }

        /**
         * 忽略记录指定的Activity
         * @param activity
         */
        fun ignoreAutoTrackActivity(activity: Class<*>) {
            mIgnoredActivities.add(activity.canonicalName)
        }

        /**
         *
         * 移除忽略的Activity
         * @param activity
         */
        fun removeIgnoredActivity(activity: Class<*>) {
            if (mIgnoredActivities.contains(activity.canonicalName)) {
                mIgnoredActivities.remove(activity.canonicalName)
            }
        }


    }
}

