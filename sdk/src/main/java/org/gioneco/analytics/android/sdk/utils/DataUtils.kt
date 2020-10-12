package org.gioneco.analytics.android.sdk.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CheckedTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton

import org.json.JSONException
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.HashMap
import java.util.Locale

import androidx.appcompat.widget.SwitchCompat
import org.gioneco.analytics.android.sdk.http.DataAPI

object DataUtils {
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss" + ".SSS", Locale.CHINA)
    /**
     * JSONObject 字符串转换
     */
    @Throws(JSONException::class)
    fun mergeJSONObject(source: JSONObject, dest: JSONObject) {
        val superPropertiesIterator = source.keys()
        while (superPropertiesIterator.hasNext()) {
            val key = superPropertiesIterator.next()
            val value = source.get(key)
            if (value is Date) {
                synchronized(mDateFormat) {
                    dest.put(key, mDateFormat.format(value))
                }
            } else {
                dest.put(key, value)
            }
        }
    }

    fun getDeviceInfo(context: Context): Map<String, Any> {
        val deviceInfo = HashMap<String, Any>()
        run {
            deviceInfo["\$lib"] = "Android"
            deviceInfo["\$lib_version"] = DataAPI.SDK_VERSION
            deviceInfo["\$os"] = "Android"
            deviceInfo["\$os_version"] = if (Build.VERSION.RELEASE == null) "UNKNOWN" else Build.VERSION.RELEASE
            deviceInfo["\$manufacturer"] = if (Build.MANUFACTURER == null) "UNKNOWN" else Build.MANUFACTURER
            if (TextUtils.isEmpty(Build.MODEL)) {
                deviceInfo["\$model"] = "UNKNOWN"
            } else {
                deviceInfo["\$model"] = Build.MODEL.trim { it <= ' ' }
            }

            try {
                val manager = context.packageManager
                val packageInfo = manager.getPackageInfo(context.packageName, 0)
                deviceInfo["\$app_version"] = packageInfo.versionName

                val labelRes = packageInfo.applicationInfo.labelRes
                deviceInfo["\$app_name"] = context.resources.getString(labelRes)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val displayMetrics = context.resources.displayMetrics
            deviceInfo["\$screen_height"] = displayMetrics.heightPixels
            deviceInfo["\$screen_width"] = displayMetrics.widthPixels

            return Collections.unmodifiableMap(deviceInfo)
        }
    }

    /**
     * 获取 Android ID
     *
     * @param mContext Context
     * @return String
     */
    @SuppressLint("HardwareIds")
    fun getAndroidID(mContext: Context): String {
        var androidID = ""
        try {
            androidID = Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return androidID
    }

    /**
     * 获取 view 的 anroid:id 对应的字符串
     *
     * @param view View
     * @return String
     */
    fun getViewId(view: View): String? {
        var idString: String? = null
        try {
            if (view.id != View.NO_ID) {
                idString = view.context.resources.getResourceEntryName(view.id)
            }
        } catch (e: Exception) {
            //ignore
        }

        return idString
    }

    fun getElementType(view: View?): String? {
        if (view == null) {
            return null
        }

        var viewType: String? = null
        when (view) {
            is CheckBox -> // CheckBox
                viewType = "CheckBox"
            is SwitchCompat -> viewType = "SwitchCompat"
            is RadioButton -> // RadioButton
                viewType = "RadioButton"
            is ToggleButton -> // ToggleButton
                viewType = "ToggleButton"
            is Button -> // Button
                viewType = "Button"
            is CheckedTextView -> // CheckedTextView
                viewType = "CheckedTextView"
            is TextView -> // TextView
                viewType = "TextView"
            is ImageButton -> // ImageButton
                viewType = "ImageButton"
            is ImageView -> // ImageView
                viewType = "ImageView"
            is RatingBar -> viewType = "RatingBar"
            is SeekBar -> viewType = "SeekBar"
        }
        return viewType
    }

    fun traverseViewContent(stringBuilder: StringBuilder, root: ViewGroup?): String {
        try {
            if (root == null) {
                return stringBuilder.toString()
            }

            val childCount = root.childCount
            for (i in 0 until childCount) {
                val child = root.getChildAt(i)

                if (child.visibility != View.VISIBLE) {
                    continue
                }

                if (child is ViewGroup) {
                    traverseViewContent(stringBuilder, child)
                } else {
                    var viewText: CharSequence? = null
                    if (child is CheckBox) {
                        viewText = child.text
                    } else if (child is SwitchCompat) {
                        viewText = child.textOn
                    } else if (child is RadioButton) {
                        viewText = child.text
                    } else if (child is ToggleButton) {
                        val isChecked = child.isChecked
                        if (isChecked) {
                            viewText = child.textOn
                        } else {
                            viewText = child.textOff
                        }
                    } else if (child is Button) {
                        viewText = child.text
                    } else if (child is CheckedTextView) {
                        viewText = child.text
                    } else if (child is TextView) {
                        viewText = child.text
                    } else if (child is ImageView) {
                        if (!TextUtils.isEmpty(child.contentDescription)) {
                            viewText = child.contentDescription.toString()
                        }
                    }

                    if (!TextUtils.isEmpty(viewText)) {
                        stringBuilder.append(viewText!!.toString())
                        stringBuilder.append("-")
                    }
                }
            }
            return stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return stringBuilder.toString()
        }

    }

    /**
     * 获取 View 上显示的文本
     *
     * @param view View
     * @return String
     */
    fun getElementContent(view: View?): String? {
        if (view == null) {
            return null
        }

        var viewText: CharSequence? = null
        when (view) {
            is CheckBox -> { // CheckBox
                val checkBox = view as CheckBox?
                viewText = checkBox!!.text
            }
            is SwitchCompat -> {
                val switchCompat = view as SwitchCompat?
                viewText = switchCompat!!.textOn
            }
            is RadioButton -> { // RadioButton
                val radioButton = view as RadioButton?
                viewText = radioButton!!.text
            }
            is ToggleButton -> { // ToggleButton
                val toggleButton = view as ToggleButton?
                val isChecked = toggleButton!!.isChecked
                viewText = if (isChecked) {
                    toggleButton.textOn
                } else {
                    toggleButton.textOff
                }
            }
            is Button -> { // Button
                val button = view as Button?
                viewText = button!!.text
            }
            is CheckedTextView -> { // CheckedTextView
                val textView = view as CheckedTextView?
                viewText = textView!!.text
            }
            is TextView -> { // TextView
                val textView = view as TextView?
                viewText = textView!!.text
            }
            is SeekBar -> {
                val seekBar = view as SeekBar?
                viewText = seekBar!!.progress.toString()
            }
            is RatingBar -> {
                val ratingBar = view as RatingBar?
                viewText = ratingBar!!.rating.toString()
            }
        }
        return viewText?.toString()
    }

    /**
     * 获取 View 所属 Activity
     *
     * @param view View
     * @return Activity
     */
    fun getActivityFromView(view: View?): Activity? {
        var activity: Activity? = null
        if (view == null) {
            return null
        }

        try {
            var context: Context? = view.context
            if (context != null) {
                if (context is Activity) {
                    activity = context
                } else if (context is ContextWrapper) {
                    while (context !is Activity && context is ContextWrapper) {
                        context = context.baseContext
                    }
                    if (context is Activity) {
                        activity = context
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return activity
    }

    fun getActivityFromContext(context: Context?): Activity? {
        var context = context
        var activity: Activity? = null
        try {
            if (context != null) {
                if (context is Activity) {
                    activity = context
                } else if (context is ContextWrapper) {
                    while (context !is Activity && context is ContextWrapper) {
                        context = context.baseContext
                    }
                    if (context is Activity) {
                        activity = context
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return activity
    }

    private fun addIndentBlank(sb: StringBuilder, indent: Int) {
        try {
            for (i in 0 until indent) {
                sb.append('\t')
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun formatJson(jsonStr: String?): String {
        try {
            if (null == jsonStr || "" == jsonStr) {
                return ""
            }
            val sb = StringBuilder()
            var last: Char
            var current = '\u0000'
            var indent = 0
            var isInQuotationMarks = false
            for (i in 0 until jsonStr.length) {
                last = current
                current = jsonStr[i]
                when (current) {
                    '"' -> {
                        if (last != '\\') {
                            isInQuotationMarks = !isInQuotationMarks
                        }
                        sb.append(current)
                    }
                    '{', '[' -> {
                        sb.append(current)
                        if (!isInQuotationMarks) {
                            sb.append('\n')
                            indent++
                            addIndentBlank(sb, indent)
                        }
                    }
                    '}', ']' -> {
                        if (!isInQuotationMarks) {
                            sb.append('\n')
                            indent--
                            addIndentBlank(sb, indent)
                        }
                        sb.append(current)
                    }
                    ',' -> {
                        sb.append(current)
                        if (last != '\\' && !isInQuotationMarks) {
                            sb.append('\n')
                            addIndentBlank(sb, indent)
                        }
                    }
                    else -> sb.append(current)
                }
            }

            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }
}
