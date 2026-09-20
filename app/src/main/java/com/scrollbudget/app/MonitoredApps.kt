package com.scrollbudget.app

/**
 * Central place to define which apps are monitored and their daily budgets.
 * Add more apps here later (Twitter/X, TikTok, etc.) — no other code needs to change.
 */
object MonitoredApps {

    data class AppConfig(
        val packageName: String,
        val displayName: String,
        val dailyBudgetMillis: Long
    )

    val list = listOf(
        AppConfig(
            packageName = "com.instagram.android",
            displayName = "Instagram",
            dailyBudgetMillis = 30 * 60 * 1000L // 30 minutes
        ),
        AppConfig(
            packageName = "com.google.android.youtube",
            displayName = "YouTube",
            dailyBudgetMillis = 30 * 60 * 1000L // 30 minutes
        )
    )

    fun findByPackage(pkg: String): AppConfig? =
        list.find { it.packageName == pkg }

    // Each puzzle solve extends the budget by this much.
    const val EXTENSION_MILLIS = 10 * 60 * 1000L // 10 minutes

    // After this many extensions in one day, no more puzzles — just the final message.
    const val MAX_EXTENSIONS_PER_DAY = 3
}
