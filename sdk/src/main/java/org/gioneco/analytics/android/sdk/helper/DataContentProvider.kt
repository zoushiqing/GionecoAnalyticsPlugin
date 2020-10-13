package org.gioneco.analytics.android.sdk.helper

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

class DataContentProvider : ContentProvider() {
    private var mContentResolver: ContentResolver? = null

    override fun onCreate(): Boolean {
        if (context != null) {
            val packName = context!!.packageName
            uriMatcher.addURI("$packName.DataContentProvider", DataTable.APP_STARTED.getName(), APP_START)
            uriMatcher.addURI("$packName.DataContentProvider", DataTable.APP_END_STATE.getName(), APP_END_STATE)
            uriMatcher.addURI("$packName.DataContentProvider", DataTable.APP_PAUSED_TIME.getName(), APP_PAUSED_TIME)
            sharedPreferences = context!!.getSharedPreferences("com.sensorsdata.analytics.android.sdk.SensorsDataAPI", Context.MODE_PRIVATE)
            mEditor = sharedPreferences!!.edit()
            mEditor!!.apply()
            mContentResolver = context!!.contentResolver
        }
        return false
    }


    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        if (contentValues == null) {
            return uri
        }
        val code = uriMatcher.match(uri)
        when (code) {
            APP_START -> {
                val appStart = contentValues.getAsBoolean(DatabaseHelper.APP_STARTED)!!
                mEditor!!.putBoolean(DatabaseHelper.APP_STARTED, appStart)
                mContentResolver!!.notifyChange(uri, null)
            }
            APP_END_STATE -> {
                val appEnd = contentValues.getAsBoolean(DatabaseHelper.APP_END_STATE)!!
                mEditor!!.putBoolean(DatabaseHelper.APP_END_STATE, appEnd)
            }
            APP_PAUSED_TIME -> {
                val pausedTime = contentValues.getAsLong(DatabaseHelper.APP_PAUSED_TIME)!!
                mEditor!!.putLong(DatabaseHelper.APP_PAUSED_TIME, pausedTime)
            }
        }
        mEditor!!.commit()
        return uri
    }

    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? {
        val code = uriMatcher.match(uri)
        var matrixCursor: MatrixCursor? = null
        when (code) {
            APP_START -> {
                val appStart = if (sharedPreferences!!.getBoolean(DatabaseHelper.APP_STARTED, true)) 1 else 0
                matrixCursor = MatrixCursor(arrayOf(DatabaseHelper.APP_STARTED))
                matrixCursor.addRow(arrayOf<Any>(appStart))
            }
            APP_END_STATE -> {
                val appEnd = if (sharedPreferences!!.getBoolean(DatabaseHelper.APP_END_STATE, true)) 1 else 0
                matrixCursor = MatrixCursor(arrayOf(DatabaseHelper.APP_END_STATE))
                matrixCursor.addRow(arrayOf<Any>(appEnd))
            }
            APP_PAUSED_TIME -> {
                val pausedTime = sharedPreferences!!.getLong(DatabaseHelper.APP_PAUSED_TIME, 0)
                matrixCursor = MatrixCursor(arrayOf(DatabaseHelper.APP_PAUSED_TIME))
                matrixCursor.addRow(arrayOf<Any>(pausedTime))
            }
        }
        return matrixCursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        return 0
    }

    companion object {
        private val APP_START = 1
        private val APP_END_STATE = 2
        private val APP_PAUSED_TIME = 3

        private var sharedPreferences: SharedPreferences? = null
        private var mEditor: SharedPreferences.Editor? = null
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }
}
