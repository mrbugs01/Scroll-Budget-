package com.scrollbudget.app

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tracks how long the user has spent in each monitored app today,
 * and how many puzzle-extensions they've used today.
 * Everything is keyed by today's date, so it naturally resets at midnight.
 */
class UsageTracker(context: Context) {

    private val prefs = context.getSharedPreferences("scroll_budget_prefs", Context.MODE_PRIVATE)

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun timeKey(pkg: String) = "time_${todayKey()}_$pkg"
    private fun extensionKey(pkg: String) = "ext_${todayKey()}_$pkg"

    fun getUsedMillis(pkg: String): Long =
        prefs.getLong(timeKey(pkg), 0L)

    fun addUsedMillis(pkg: String, millis: Long) {
        val current = getUsedMillis(pkg)
        prefs.edit().putLong(timeKey(pkg), current + millis).apply()
    }

    fun getExtensionCount(pkg: String): Int =
        prefs.getInt(extensionKey(pkg), 0)

    fun incrementExtensionCount(pkg: String) {
        val current = getExtensionCount(pkg)
        prefs.edit().putInt(extensionKey(pkg), current + 1).apply()
    }

    /**
     * Total allowed time right now = base daily budget + (extensions used * extension length)
     */
    fun getAllowedMillis(pkg: String): Long {
        val config = MonitoredApps.findByPackage(pkg) ?: return 0L
        val extensions = getExtensionCount(pkg)
        return config.dailyBudgetMillis + (extensions * MonitoredApps.EXTENSION_MILLIS)
    }

    fun isOverBudget(pkg: String): Boolean =
        getUsedMillis(pkg) >= getAllowedMillis(pkg)

    fun hasExtensionsLeft(pkg: String): Boolean =
        getExtensionCount(pkg) < MonitoredApps.MAX_EXTENSIONS_PER_DAY
}
