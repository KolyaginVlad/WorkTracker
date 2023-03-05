package ru.kolyagin.worktracker.utils.log

import android.os.Bundle
import android.util.Log
import ru.kolyagin.worktracker.utils.analytics.AnalyticsEvent
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.Serializable
import javax.inject.Inject

class LoggerImpl @Inject constructor(
    private val analytics: FirebaseAnalytics,
) : Logger {

    override fun error(throwable: Throwable) {
        throwable.printStackTrace()
        //TODO Добавить отправку в Crashlytics
    }

    override fun error(message: String) {
        Log.e(TAG, message)
    }

    override fun debug(message: String) {
        Log.d(TAG, message)
    }

    override fun info(message: String) {
        Log.i(TAG, message)
    }

    override fun event(
        event: AnalyticsEvent,
    ) {
        val bundle = Bundle().apply {
            event.arguments.forEach { entry ->
                val value = entry.value
                if (value is Serializable?) {
                    putSerializable(entry.key, value)
                } else {
                    putString(entry.key, value.toString())
                }
            }
        }
        analytics.logEvent(event.name, bundle)
        info("Sending event ${event.name} with args ${event.arguments}")
    }
    companion object {
        const val TAG = "ExampleT"
    }
}