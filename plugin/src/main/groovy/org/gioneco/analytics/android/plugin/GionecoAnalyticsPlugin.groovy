package org.gioneco.analytics.android.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


class GionecoAnalyticsPlugin implements Plugin<Project> {
    void apply(Project project) {

        GionecoAnalyticsExtension extension = project.extensions.create("gionecoAnalytics", GionecoAnalyticsExtension)

        boolean disableSensorsAnalyticsPlugin = false
        Properties properties = new Properties()
        if (project.rootProject.file('gradle.properties').exists()) {
            properties.load(project.rootProject.file('gradle.properties').newDataInputStream())
            disableSensorsAnalyticsPlugin = Boolean.parseBoolean(properties.getProperty("GIONECO_ANALYTICS_DISABLE_PLUGIN", "false"))
        }
        println("------------智元汇插件--------------"+disableSensorsAnalyticsPlugin)
        if (!disableSensorsAnalyticsPlugin) {
            AppExtension appExtension = project.extensions.findByType(AppExtension.class)
            extension.disableAppClick = disableSensorsAnalyticsPlugin
            appExtension.registerTransform(new GionecoAnalyticsTransform(project, extension))
        } else {
            println("------------您已关闭了智元汇插件--------------")
        }
    }
}