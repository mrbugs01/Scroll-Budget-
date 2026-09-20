package com.scrollbudget.app

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

/**
 * Detects which app is currently in the foreground using window-state-change
 * accessibility events. While a monitored app (Instagram/YouTube) is in front,
 * a repeating tick adds elapsed time to today's usage total. When the budget
 * for that app is exceeded, it shows the puzzle overlay.
 */
class AppMonitorService : AccessibilityService() {

    private lateinit var usageTracker: UsageTracker
    private lateinit var overlayManager: OverlayManager
    private lateinit var awayStreakTracker: AwayStreakTracker

    private var currentTrackedPackage: String? = null
    private var lastTickTime: Long = 0L

    private val tickHandler = Handler(Looper.getMainLooper())
    private val tickIntervalMillis = 1000L // check every second

    private val tickRunnable = object : Runnable {
        override fun run() {
            val pkg = currentTrackedPackage
            if (pkg != null) {
                val now = System.currentTimeMillis()
                val elapsed = now - lastTickTime
                lastTickTime = now
                usageTracker.addUsedMillis(pkg, elapsed)
                checkBudget(pkg)
            }
            checkAwayMilestone()
            tickHandler.postDelayed(this, tickIntervalMillis)
        }
    }

    private fun checkAwayMilestone() {
        val hours = awayStreakTracker.elapsedHours()
        val tier = AwayMilestones.tierForHours(hours) ?: return
        val highestShown = awayStreakTracker.highestMilestoneShown()
        if (tier.hour > highestShown) {
            awayStreakTracker.markMilestoneShown(tier.hour)
            MilestoneNotifier.notify(applicationContext, tier)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        usageTracker = UsageTracker(applicationContext)
        overlayManager = OverlayManager(applicationContext)
        awayStreakTracker = AwayStreakTracker(applicationContext)
        tickHandler.postDelayed(tickRunnable, tickIntervalMillis)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        val config = MonitoredApps.findByPackage(pkg)
        if (config != null) {
            // Entered (or still in) a monitored app.
            if (currentTrackedPackage != pkg) {
                currentTrackedPackage = pkg
                lastTickTime = System.currentTimeMillis()
                awayStreakTracker.recordSocialMediaUse()
            }
            // If already over budget the moment they open it, show overlay immediately.
            checkBudget(pkg)
        } else {
            // Left the monitored app (switched to launcher, another app, etc).
            currentTrackedPackage = null
        }
    }

    private fun checkBudget(pkg: String) {
        if (overlayManager.isShowing()) return
        if (!usageTracker.isOverBudget(pkg)) return

        val config = MonitoredApps.findByPackage(pkg) ?: return

        if (usageTracker.hasExtensionsLeft(pkg)) {
            overlayManager.showPuzzle(
                appName = config.displayName,
                onSolved = {
                    usageTracker.incrementExtensionCount(pkg)
                },
                onDismiss = {
                    // User chose to stop instead of solving — nothing extra to do,
                    // overlay will simply reappear on next tick since still over budget
                    // unless they leave the app.
                }
            )
        } else {
            val usedMinutes = usageTracker.getUsedMillis(pkg) / 60000L
            overlayManager.showFinalMessage(
                appName = config.displayName,
                usedMinutes = usedMinutes,
                onDismiss = { }
            )
        }
    }

    override fun onInterrupt() {
        tickHandler.removeCallbacks(tickRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        tickHandler.removeCallbacks(tickRunnable)
        overlayManager.remove()
    }
}
