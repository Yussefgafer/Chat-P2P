package com.example.p2pchat.core.utils

import android.content.Context
import android.content.Intent
import android.os.Process
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * Custom crash handler for better error reporting and recovery
 */
class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        try {
            // Log the crash
            logCrash(exception)
            
            // Save crash report
            saveCrashReport(exception)
            
            // Attempt graceful recovery for certain exceptions
            if (attemptRecovery(exception)) {
                return
            }
            
            // Restart the app if possible
            restartApp()
            
        } catch (e: Exception) {
            Timber.e(e, "Error in crash handler")
        } finally {
            // Let the default handler take over
            defaultHandler?.uncaughtException(thread, exception)
        }
    }

    private fun logCrash(exception: Throwable) {
        Timber.e(exception, "Uncaught exception occurred")
        
        val stackTrace = getStackTrace(exception)
        Timber.e("Stack trace: $stackTrace")
    }

    private fun saveCrashReport(exception: Throwable) {
        try {
            val crashReport = buildCrashReport(exception)
            
            // Save to internal storage
            val fileName = "crash_${System.currentTimeMillis()}.txt"
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(crashReport.toByteArray())
            }
            
            Timber.d("Crash report saved: $fileName")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save crash report")
        }
    }

    private fun buildCrashReport(exception: Throwable): String {
        return buildString {
            appendLine("=== P2P Chat Crash Report ===")
            appendLine("Timestamp: ${System.currentTimeMillis()}")
            appendLine("Thread: ${Thread.currentThread().name}")
            appendLine("Exception: ${exception.javaClass.simpleName}")
            appendLine("Message: ${exception.message}")
            appendLine()
            appendLine("Stack Trace:")
            appendLine(getStackTrace(exception))
            appendLine()
            appendLine("Device Info:")
            appendLine("Model: ${android.os.Build.MODEL}")
            appendLine("Manufacturer: ${android.os.Build.MANUFACTURER}")
            appendLine("Android Version: ${android.os.Build.VERSION.RELEASE}")
            appendLine("SDK Level: ${android.os.Build.VERSION.SDK_INT}")
            appendLine()
            appendLine("App Info:")
            appendLine("Package: ${context.packageName}")
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                appendLine("Version: ${packageInfo.versionName}")
                appendLine("Version Code: ${packageInfo.versionCode}")
            } catch (e: Exception) {
                appendLine("Version: Unknown")
            }
        }
    }

    private fun getStackTrace(exception: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        exception.printStackTrace(printWriter)
        return stringWriter.toString()
    }

    private fun attemptRecovery(exception: Throwable): Boolean {
        return when (exception) {
            is OutOfMemoryError -> {
                Timber.w("Attempting recovery from OutOfMemoryError")
                // Clear caches, force garbage collection
                System.gc()
                Runtime.getRuntime().gc()
                true
            }
            is SecurityException -> {
                Timber.w("Security exception occurred, continuing execution")
                true
            }
            else -> false
        }
    }

    private fun restartApp() {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
            
            // Kill current process
            Process.killProcess(Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            Timber.e(e, "Failed to restart app")
        }
    }
}
