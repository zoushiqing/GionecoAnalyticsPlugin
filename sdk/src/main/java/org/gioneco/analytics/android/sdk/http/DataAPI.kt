package org.gioneco.analytics.android.sdk.http

import android.app.Application
import android.util.Log

import org.json.JSONObject

import androidx.annotation.Keep
import org.gioneco.analytics.android.sdk.helper.DataAppViewScreenHelper
import org.gioneco.analytics.android.sdk.utils.DataUtils

@Keep
class DataAPI private constructor() {
    private val mTAG = "DataAPI"

    companion object {
        const val SDK_VERSION = "1.0.0"
        @Volatile
        private var instance: DataAPI? = null
        /**
         * 设备信息
         */
        private lateinit var mDeviceInfo: Map<String, Any>
        /**
         * 设备id
         */
        var mDeviceId: String = ""
        private lateinit var application: Application
        @Keep
        fun init(application: Application): DataAPI {
            mDeviceId = DataUtils.getAndroidID(application.applicationContext)
            mDeviceInfo = DataUtils.getDeviceInfo(application.applicationContext)
            DataAppViewScreenHelper.registerActivityLifecycleCallbacks(application)
            Companion.application = application
            return getInstance()
        }

        fun getInstance() = instance ?: synchronized(this) {
            instance
                    ?: DataAPI().also { instance = it }
        }
    }

    /**
     * Track 事件
     *
     * @param eventName  String 事件名称
     * @param properties JSONObject 事件属性
     */
    @Keep
    fun track(eventName: String, properties: JSONObject?) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("event", eventName)
            jsonObject.put("device_id", mDeviceId)

            val sendProperties = JSONObject(mDeviceInfo)

            if (properties != null) {
                DataUtils.mergeJSONObject(properties, sendProperties)
            }

            jsonObject.put("properties", sendProperties)
            jsonObject.put("time", System.currentTimeMillis())
            //
            //            Log.i(TAG, DataUtils.formatJson(jsonObject.toString()));
            Log.i(mTAG, "上传数据。。。。。" + properties!!)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
