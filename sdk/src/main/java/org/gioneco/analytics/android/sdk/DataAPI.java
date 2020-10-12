package org.gioneco.analytics.android.sdk;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import java.util.Map;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@Keep
public class DataAPI {
    private final String TAG = this.getClass().getSimpleName();
    public static final String SDK_VERSION = "1.0.0";
    private static DataAPI INSTANCE;
    private static final Object mLock = new Object();
    private static Map<String, Object> mDeviceInfo;
    private String mDeviceId;

    @Keep
    @SuppressWarnings("UnusedReturnValue")
    public static DataAPI init(Application application) {
        synchronized (mLock) {
            if (null == INSTANCE) {
                INSTANCE = new DataAPI(application);
            }
            return INSTANCE;
        }
    }

    @Keep
    public static DataAPI getInstance() {
        return INSTANCE;
    }

    private DataAPI(Application application) {
        mDeviceId = DataPrivate.getAndroidID(application.getApplicationContext());
        mDeviceInfo = DataPrivate.getDeviceInfo(application.getApplicationContext());
    }

    /**
     * Track 事件
     *
     * @param eventName  String 事件名称
     * @param properties JSONObject 事件属性
     */
    @Keep
    public void track(@NonNull final String eventName, @Nullable JSONObject properties) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", eventName);
            jsonObject.put("device_id", mDeviceId);

            JSONObject sendProperties = new JSONObject(mDeviceInfo);

            if (properties != null) {
                DataPrivate.mergeJSONObject(properties, sendProperties);
            }

            jsonObject.put("properties", sendProperties);
            jsonObject.put("time", System.currentTimeMillis());
//
//            Log.i(TAG, DataPrivate.formatJson(jsonObject.toString()));
            Log.i(TAG, "上传数据。。。。。"+properties);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
