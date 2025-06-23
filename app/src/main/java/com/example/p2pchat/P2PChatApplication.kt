package com.example.p2pchat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.example.p2pchat.core.theme.DynamicThemeManager
import com.example.p2pchat.core.security.SecurityManager
import com.example.p2pchat.core.utils.CrashHandler
// import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
// import javax.inject.Inject

/**
 * Main Application class for P2P Chat
 * Handles global initialization and dependency injection
 */
// @HiltAndroidApp
class P2PChatApplication : Application() {

    // @Inject
    lateinit var dynamicThemeManager: DynamicThemeManager

    // @Inject
    lateinit var securityManager: SecurityManager

    companion object {
        const val CHANNEL_ID_MESSAGES = "messages_channel"
        const val CHANNEL_ID_CONNECTIONS = "connections_channel"
        const val CHANNEL_ID_AI_ASSISTANT = "ai_assistant_channel"

        lateinit var instance: P2PChatApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize logging
        initializeLogging()

        // Initialize crash handling
        initializeCrashHandling()

        // Initialize security
        // initializeSecurity()

        // Initialize theme system
        // initializeThemeSystem()

        // Create notification channels
        createNotificationChannels()

        Timber.d("P2P Chat Application initialized successfully")
    }

    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In production, you might want to use a different tree
            // that logs to a file or crash reporting service
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Custom logging implementation for production
                }
            })
        }
    }

    private fun initializeCrashHandling() {
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }

    private fun initializeSecurity() {
        // securityManager.initialize()
    }

    private fun initializeThemeSystem() {
        // dynamicThemeManager.initialize(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Messages Channel
            val messagesChannel = NotificationChannel(
                CHANNEL_ID_MESSAGES,
                getString(R.string.channel_messages_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_messages_description)
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // Connections Channel
            val connectionsChannel = NotificationChannel(
                CHANNEL_ID_CONNECTIONS,
                getString(R.string.channel_connections_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_connections_description)
                enableLights(true)
                enableVibration(false)
            }

            // AI Assistant Channel
            val aiAssistantChannel = NotificationChannel(
                CHANNEL_ID_AI_ASSISTANT,
                getString(R.string.channel_ai_assistant_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_ai_assistant_description)
                enableLights(false)
                enableVibration(false)
            }

            notificationManager.createNotificationChannels(
                listOf(messagesChannel, connectionsChannel, aiAssistantChannel)
            )
        }
    }

    /**
     * Get application context safely
     */
    fun getAppContext(): Context = applicationContext

    /**
     * Check if notifications are enabled
     */
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }
}
