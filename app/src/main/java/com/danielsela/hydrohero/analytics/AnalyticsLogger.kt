package com.danielsela.hydrohero.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * Small abstraction so ViewModels can log analytics without depending on Android Context.
 */
interface AnalyticsLogger {
    fun logEvent(name: String, params: Map<String, Any?> = emptyMap())
    fun logScreen(screenName: String, screenClass: String = "MainActivity")
    fun setUserProperty(name: String, value: String?)
}

object NoOpAnalyticsLogger : AnalyticsLogger {
    override fun logEvent(name: String, params: Map<String, Any?>) = Unit
    override fun logScreen(screenName: String, screenClass: String) = Unit
    override fun setUserProperty(name: String, value: String?) = Unit
}

class FirebaseAnalyticsLogger(
    @Suppress("UNUSED_PARAMETER") context: Context
) : AnalyticsLogger {
    private val analytics = Firebase.analytics

    override fun logEvent(name: String, params: Map<String, Any?>) {
        analytics.logEvent(name, params.toFirebaseBundle())
    }

    override fun logScreen(screenName: String, screenClass: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun setUserProperty(name: String, value: String?) {
        analytics.setUserProperty(name, value)
    }
}

private fun Map<String, Any?>.toFirebaseBundle(): Bundle {
    val b = Bundle()
    forEach { (k, v) ->
        when (v) {
            null -> Unit
            is String -> b.putString(k, v)
            is Int -> b.putLong(k, v.toLong())
            is Long -> b.putLong(k, v)
            is Float -> b.putDouble(k, v.toDouble())
            is Double -> b.putDouble(k, v)
            is Boolean -> b.putString(k, if (v) "true" else "false")
            else -> b.putString(k, v.toString())
        }
    }
    return b
}

