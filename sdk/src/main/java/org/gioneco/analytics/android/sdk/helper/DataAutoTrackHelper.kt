package org.gioneco.analytics.android.sdk.helper

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*

import org.json.JSONObject

import java.util.Locale

import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import org.gioneco.analytics.android.sdk.http.DataAPI
import org.gioneco.analytics.android.sdk.utils.DataUtils

/**
 * 点击事件统计helper
 */
object DataAutoTrackHelper {
    /**
     * dialog 被点击，自动埋点
     */
    @JvmStatic
    @Keep
    fun trackViewOnClick(dialogInterface: DialogInterface, whichButton: Int) {
        try {
            var dialog: Dialog? = null
            if (dialogInterface is Dialog) {
                dialog = dialogInterface
            }

            if (dialog == null) {
                return
            }

            val context = dialog.context
            //将Context转成Activity
            var activity = DataUtils.getActivityFromContext(context)

            if (activity == null) {
                activity = dialog.ownerActivity
            }

            val properties = JSONObject()
            //$screen_name & $title
            if (activity != null) {
                properties.put("\$activity", activity.javaClass.canonicalName)
            }

            var button: Button? = null
            if (dialog is android.app.AlertDialog) {
                button = dialog.getButton(whichButton)
            } else if (dialog is AlertDialog) {
                button = dialog.getButton(whichButton)
            }

            if (button != null) {
                properties.put("\$element_content", button.text)
            }

            properties.put("\$element_type", "Dialog")

            DataAPI.getInstance().track("\$AppClick", properties)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * CompoundButton 被点击，自动埋点
     */
    @JvmStatic
    fun trackViewOnClick(view: CompoundButton, isChecked: Boolean) {
        try {
            val context = view.context ?: return

            val properties = JSONObject()

            val activity = DataUtils.getActivityFromContext(context)

            try {
                val idString = context.resources.getResourceEntryName(view.id)
                if (!TextUtils.isEmpty(idString)) {
                    properties.put("\$element_id", idString)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (activity != null) {
                properties.put("\$activity", activity.javaClass.canonicalName)
            }

            var viewText: String? = null
            val viewType: String?
            if (view is CheckBox) {
                viewType = "CheckBox"
                if (!TextUtils.isEmpty(view.text)) {
                    viewText = view.text.toString()
                }
            } else if (view is SwitchCompat) {
                viewType = "SwitchCompat"
                if (isChecked) {
                    if (!TextUtils.isEmpty(view.textOn)) {
                        viewText = view.textOn.toString()
                    }
                } else {
                    if (!TextUtils.isEmpty(view.textOff)) {
                        viewText = view.textOff.toString()
                    }
                }
            } else if (view is ToggleButton) {
                viewType = "ToggleButton"
                if (isChecked) {
                    if (!TextUtils.isEmpty(view.textOn)) {
                        viewText = view.textOn.toString()
                    }
                } else {
                    if (!TextUtils.isEmpty(view.textOff)) {
                        viewText = view.textOff.toString()
                    }
                }
            } else if (view is RadioButton) {
                viewType = "RadioButton"
                if (!TextUtils.isEmpty(view.text)) {
                    viewText = view.text.toString()
                }
            } else {
                viewType = view.javaClass.canonicalName
            }

            //Content
            if (!TextUtils.isEmpty(viewText)) {
                properties.put("\$element_content", viewText)
            }

            if (!TextUtils.isEmpty(viewType)) {
                properties.put("\$element_type", viewType)
            }

            properties.put("isChecked", isChecked)

            DataAPI.getInstance().track("\$AppClick", properties)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Dialog 被点击，自动埋点
     */
    @JvmStatic
    @Keep
    fun trackViewOnClick(dialogInterface: DialogInterface, whichButton: Int, isChecked: Boolean) {
        try {
            var dialog: Dialog? = null
            if (dialogInterface is Dialog) {
                dialog = dialogInterface
            }

            if (dialog == null) {
                return
            }

            val context = dialog.context
            //将Context转成Activity
            var activity = DataUtils.getActivityFromContext(context)

            if (activity == null) {
                activity = dialog.ownerActivity
            }

            val properties = JSONObject()
            //$screen_name & $title
            if (activity != null) {
                properties.put("\$activity", activity.javaClass.canonicalName)
            }

            var listView: ListView? = null
            if (dialog is android.app.AlertDialog) {
                listView = dialog.listView
            } else if (dialog is AlertDialog) {
                listView = dialog.listView
            }

            if (listView != null) {
                val listAdapter = listView.adapter
                val `object` = listAdapter.getItem(whichButton)
                if (`object` != null) {
                    if (`object` is String) {
                        properties.put("\$element_content", `object`)
                    }
                }
            }

            properties.put("isChecked", isChecked)
            properties.put("\$element_type", "Dialog")

            DataAPI.getInstance().track("\$AppClick", properties)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * MenuItem 被点击，自动埋点
     *
     * @param object   Object
     * @param menuItem MenuItem
     */
    @JvmStatic
    @Keep
    fun trackViewOnClick(`object`: Any, menuItem: MenuItem) {
        try {
            var context: Context? = null
            if (`object` is Context) {
                context = `object`
            }
            val jsonObject = JSONObject()
            jsonObject.put("\$element_type", "menuItem")

            jsonObject.put("\$element_content", menuItem.title)

            if (context != null) {
                var idString: String? = null
                try {
                    idString = context.resources.getResourceEntryName(menuItem.itemId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (!TextUtils.isEmpty(idString)) {
                    jsonObject.put("\$element_id", idString)
                }

                val activity = DataUtils.getActivityFromContext(context)
                if (activity != null) {
                    jsonObject.put("\$activity", activity.javaClass.canonicalName)
                }
            }

            DataAPI.getInstance().track("\$AppClick", jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * trackTabHost 被点击，自动埋点
     */
    @JvmStatic
    @Keep
    fun trackTabHost(tabName: String) {
        try {
            val properties = JSONObject()

            properties.put("\$element_type", "TabHost")
            properties.put("\$element_content", tabName)
            DataAPI.getInstance().track("\$AppClick", properties)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * ExpandableListView 被点击，自动埋点
     */
    @JvmStatic
    @Keep
    fun trackExpandableListViewGroupOnClick(expandableListView: ExpandableListView, view: View,
                                            groupPosition: Int) {
        trackExpandableListViewChildOnClick(expandableListView, view, groupPosition, -1)
    }

    /**
     * ExpandableListView 被点击，自动埋点
     */
    @JvmStatic
    @Keep
    fun trackExpandableListViewChildOnClick(expandableListView: ExpandableListView, view: View,
                                            groupPosition: Int, childPosition: Int) {
        try {
            val context = expandableListView.context ?: return

            val properties = JSONObject()
            val activity = DataUtils.getActivityFromContext(context)
            if (activity != null) {
                properties.put("\$activity", activity.javaClass.canonicalName)
            }

            if (childPosition != -1) {
                properties.put("\$element_position", String.format(Locale.CHINA, "%d:%d", groupPosition, childPosition))
            } else {
                properties.put("\$element_position", String.format(Locale.CHINA, "%d", groupPosition))
            }

            val idString = DataUtils.getViewId(expandableListView)
            if (!TextUtils.isEmpty(idString)) {
                properties.put("\$element_id", idString)
            }

            properties.put("\$element_type", "ExpandableListView")

            var viewText: String? = null
            if (view is ViewGroup) {
                try {
                    val stringBuilder = StringBuilder()
                    viewText = DataUtils.traverseViewContent(stringBuilder, view)
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText!!.substring(0, viewText.length - 1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            if (!TextUtils.isEmpty(viewText)) {
                properties.put("\$element_content", viewText)
            }

            DataAPI.getInstance().track("\$AppClick", properties)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * AdapterView 被点击，自动埋点
     *
     */
    @JvmStatic
    @Keep
    fun trackViewOnClick(adapterView: AdapterView<*>, view: View, position: Int) {
        try {
            val context = adapterView.context ?: return

            val properties = JSONObject()

            val activity = DataUtils.getActivityFromContext(context)
            val idString = DataUtils.getViewId(adapterView)
            if (!TextUtils.isEmpty(idString)) {
                properties.put("\$element_id", idString)
            }

            if (activity != null) {
                properties.put("\$activity", activity.javaClass.canonicalName)
            }
            properties.put("\$element_position", position.toString())

            if (adapterView is Spinner) {
                properties.put("\$element_type", "Spinner")
                val item = adapterView.getItemAtPosition(position)
                if (item != null) {
                    if (item is String) {
                        properties.put("\$element_content", item)
                    }
                }
            } else {
                if (adapterView is ListView) {
                    properties.put("\$element_type", "ListView")
                } else if (adapterView is GridView) {
                    properties.put("\$element_type", "GridView")
                }

                var viewText: String? = null
                if (view is ViewGroup) {
                    try {
                        val stringBuilder = StringBuilder()
                        viewText = DataUtils.traverseViewContent(stringBuilder, view)
                        if (!TextUtils.isEmpty(viewText)) {
                            viewText = viewText!!.substring(0, viewText.length - 1)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    viewText = DataUtils.getElementContent(view)
                }
                //$element_content
                if (!TextUtils.isEmpty(viewText)) {
                    properties.put("\$element_content", viewText)
                }
            }
            DataAPI.getInstance().track("\$AppClick", properties)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * View 被点击，自动埋点
     *
     * @param view View
     */
    @JvmStatic
    @Keep
    fun trackViewOnClick(view: View) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("\$element_type", DataUtils.getElementType(view))
            jsonObject.put("\$element_id", DataUtils.getViewId(view))
            jsonObject.put("\$element_content", DataUtils.getElementContent(view))

            val activity = DataUtils.getActivityFromView(view)
            if (activity != null) {
                jsonObject.put("\$activity", activity.javaClass.canonicalName)
            }

            DataAPI.getInstance().track("\$AppClick", jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}

