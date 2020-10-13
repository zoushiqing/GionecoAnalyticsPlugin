package org.gioneco.analytics.android.sdk.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import org.gioneco.analytics.android.sdk.http.DataAPI
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.*



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
        private lateinit var mDatabaseHelper:DatabaseHelper
        private lateinit var countDownTimer: CountDownTimer
        private const val SESSION_INTERVAL_TIME = 30 * 1000L
        private var mCurrentActivity: WeakReference<Activity>? = null

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
                DataAPI.getInstance().track("\$AppViewScreen", properties)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /**
         * 注册Application.registerActivityLifecycleCallbacks
         * @param application
         */
        fun registerActivityLifecycleCallbacks(application: Application) {
            mDatabaseHelper = DatabaseHelper(application.applicationContext, application.packageName)
            countDownTimer = object : CountDownTimer(SESSION_INTERVAL_TIME, (10 * 1000).toLong()) {
                override fun onTick(l: Long) {

                }

                override fun onFinish() {
                    if (mCurrentActivity != null) {
                        trackAppEnd(mCurrentActivity!!.get())
                    }
                }
            }
            application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

                override fun onActivityPaused(activity: Activity?) {
                    mCurrentActivity = WeakReference<Activity>(activity)
                    countDownTimer.start()
                    mDatabaseHelper.commitAppPausedTime(System.currentTimeMillis())
                }

                override fun onActivityResumed(activity: Activity?) {
                    if (activity != null) {
                        trackAppViewScreen(activity)
                    }
                }

                override fun onActivityStarted(activity: Activity?) {
                    mDatabaseHelper.commitAppStart(true)
                    val timeDiff = System.currentTimeMillis() - mDatabaseHelper.appPausedTime
                    if (timeDiff > SESSION_INTERVAL_TIME) {
                        if (!mDatabaseHelper.appEndEventState) {
                            trackAppEnd(activity)
                        }
                    }

                    if (mDatabaseHelper.appEndEventState) {
                        mDatabaseHelper.commitAppEndEventState(false)
                        trackAppStart(activity)
                    }
                }

                override fun onActivityDestroyed(activity: Activity?) {}

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

                override fun onActivityStopped(activity: Activity?) {}

                override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}

            })
        }

        /**
         * Track $AppStart 事件
         */
        private fun trackAppStart(activity: Activity?) {
            try {
                if (activity == null) {
                    return
                }
                val properties = JSONObject()
                properties.put("\$activity", activity.javaClass.canonicalName)
                properties.put("\$title", getActivityTitle(activity))
                DataAPI.getInstance().track("\$AppStart", properties)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * Track $AppEnd 事件
         */
        private fun trackAppEnd(activity: Activity?) {
            try {
                if (activity == null) {
                    return
                }
                val properties = JSONObject()
                properties.put("\$activity", activity.javaClass.canonicalName)
                properties.put("\$title", getActivityTitle(activity))
                DataAPI.getInstance().track("\$AppEnd", properties)
                mDatabaseHelper.commitAppEndEventState(true)
                mCurrentActivity = null
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        /**
         * 注册 AppStart 的监听
         */
        fun registerActivityStateObserver(application: Application) {
            application.contentResolver.registerContentObserver(mDatabaseHelper.appStartUri,
                    false, object : ContentObserver(Handler()) {
                override fun onChange(selfChange: Boolean, uri: Uri) {
                    if (mDatabaseHelper.appStartUri.equals(uri)) {
                        countDownTimer.cancel()
                    }
                }
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

