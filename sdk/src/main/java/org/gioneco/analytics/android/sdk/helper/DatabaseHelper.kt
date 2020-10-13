package org.gioneco.analytics.android.sdk.helper

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri

/*public*/ internal class DatabaseHelper(context: Context, packageName: String) {
    private val mContentResolver: ContentResolver
    val appStartUri: Uri
    private val mAppEndState: Uri
    private val mAppPausedTime: Uri

    /**
     * Return the time of Activity paused
     *
     * @return Activity paused time
     */
    val appPausedTime: Long
        get() {
            var pausedTime: Long = 0
            val cursor = mContentResolver.query(mAppPausedTime, arrayOf(APP_PAUSED_TIME), null, null, null)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    pausedTime = cursor.getLong(0)
                }
            }

            cursor?.close()
            return pausedTime
        }

    /**
     * Return the state of $AppEnd
     *
     * @return Activity End state
     */
    val appEndEventState: Boolean
        get() {
            var state = true
            val cursor = mContentResolver.query(mAppEndState, arrayOf(APP_END_STATE), null, null, null)
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    state = cursor.getInt(0) > 0
                }
            }

            cursor?.close()
            return state
        }


    init {
        mContentResolver = context.contentResolver
        appStartUri = Uri.parse("content://" + packageName + SensorsDataContentProvider + DataTable.APP_STARTED.getName())
        mAppEndState = Uri.parse("content://" + packageName + SensorsDataContentProvider + DataTable.APP_END_STATE.getName())
        mAppPausedTime = Uri.parse("content://" + packageName + SensorsDataContentProvider + DataTable.APP_PAUSED_TIME.getName())
    }


    /**
     * Add the AppStart state to the SharedPreferences
     *
     * @param appStart the ActivityState
     */
    fun commitAppStart(appStart: Boolean) {
        val contentValues = ContentValues()
        contentValues.put(APP_STARTED, appStart)
        mContentResolver.insert(appStartUri, contentValues)
    }

    /**
     * Add the Activity paused time to the SharedPreferences
     *
     * @param pausedTime Activity paused time
     */
    fun commitAppPausedTime(pausedTime: Long) {
        val contentValues = ContentValues()
        contentValues.put(APP_PAUSED_TIME, pausedTime)
        mContentResolver.insert(mAppPausedTime, contentValues)
    }

    /**
     * Add the Activity End to the SharedPreferences
     *
     * @param appEndState the Activity end state
     */
    fun commitAppEndEventState(appEndState: Boolean) {
        val contentValues = ContentValues()
        contentValues.put(APP_END_STATE, appEndState)
        mContentResolver.insert(mAppEndState, contentValues)
    }

    companion object {
        private val SensorsDataContentProvider = ".DataContentProvider/"

        val APP_STARTED = "\$app_started"
        val APP_END_STATE = "\$app_end_state"
        val APP_PAUSED_TIME = "\$app_paused_time"
    }
}
