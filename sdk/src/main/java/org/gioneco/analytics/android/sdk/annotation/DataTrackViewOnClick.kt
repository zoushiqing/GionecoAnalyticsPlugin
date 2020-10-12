package org.gioneco.analytics.android.sdk.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 在布局文件中设置onClick的点击事件 在代码中对应的方法添加这个注解
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(RetentionPolicy.RUNTIME)
annotation class DataTrackViewOnClick
