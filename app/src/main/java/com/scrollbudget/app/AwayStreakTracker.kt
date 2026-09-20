package com.scrollbudget.app

import android.content.Context

/**
 * Tracks how long it's been since the user last opened a monitored app
 * (Instagram/YouTube). The streak resets the moment they open one again.
 * Also remembers which milestone was last shown, so the congratulatory
 * dialog only appears once per milestone.
 */
class AwayStreakTracker(context: Context) {

    private val prefs = context.getSharedPreferences("scroll_budget_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_SOCIAL_USE = "last_social_media_use_millis"
        private const val KEY_HIGHEST_MILESTONE_SHOWN = "highest_milestone_shown"
    }

    /** Call this the moment a monitored app comes to the foreground. */
    fun recordSocialMediaUse() {
        prefs.edit()
            .putLong(KEY_LAST_SOCIAL_USE, System.currentTimeMillis())
            .putInt(KEY_HIGHEST_MILESTONE_SHOWN, 0) // reset milestones for the new streak
            .apply()
    }

    /** Elapsed time since the streak started (first-ever call defaults to "now", i.e. 0 elapsed). */
    fun elapsedMillis(): Long {
        val last = prefs.getLong(KEY_LAST_SOCIAL_USE, -1L)
        if (last == -1L) {
            // First time ever — start the streak now.
            prefs.edit().putLong(KEY_LAST_SOCIAL_USE, System.currentTimeMillis()).apply()
            return 0L
        }
        return (System.currentTimeMillis() - last).coerceAtLeast(0L)
    }

    fun elapsedHours(): Int = (elapsedMillis() / (60 * 60 * 1000L)).toInt()

    fun highestMilestoneShown(): Int = prefs.getInt(KEY_HIGHEST_MILESTONE_SHOWN, 0)

    fun markMilestoneShown(hour: Int) {
        prefs.edit().putInt(KEY_HIGHEST_MILESTONE_SHOWN, hour).apply()
    }
}
