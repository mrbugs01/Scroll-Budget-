package com.scrollbudget.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var awayStreakTracker: AwayStreakTracker
    private lateinit var apiKeyStore: ApiKeyStore

    private val streakHandler = Handler(Looper.getMainLooper())
    private val streakTickIntervalMillis = 1000L

    private val streakTickRunnable = object : Runnable {
        override fun run() {
            updateStreakDisplay()
            streakHandler.postDelayed(this, streakTickIntervalMillis)
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) {
            refreshPermissionRows()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        awayStreakTracker = AwayStreakTracker(applicationContext)
        apiKeyStore = ApiKeyStore(applicationContext)

        setupBookButtons()
        setupAiCoach()
        setupReminders()

        val existingKey = apiKeyStore.getKey()
        if (!existingKey.isNullOrBlank()) {
            findViewById<EditText>(R.id.apiKeyInput).setText(existingKey)
        }
    }

    // ---------- Quick Setup: rows disappear automatically once granted ----------

    private fun refreshPermissionRows() {
        val container = findViewById<LinearLayout>(R.id.permissionsContainer)
        val subtext = findViewById<TextView>(R.id.setupSubtext)
        val setupCard = findViewById<LinearLayout>(R.id.setupCard)
        container.removeAllViews()

        val accessibilityGranted = isAccessibilityServiceEnabled()
        val overlayGranted = Settings.canDrawOverlays(this)
        val notificationsGranted = areNotificationsGranted()

        var rowsAdded = 0

        if (!accessibilityGranted) {
            addPermissionRow(
                container,
                "Enable Accessibility Service",
                "Lets the app detect when Instagram/YouTube opens"
            ) { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
            rowsAdded++
        }

        if (!overlayGranted) {
            addPermissionRow(
                container,
                "Enable Overlay Permission",
                "Needed to show the puzzle screen over other apps"
            ) {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }
            rowsAdded++
        }

        if (!notificationsGranted) {
            addPermissionRow(
                container,
                "Enable Notifications",
                "So you get milestone rewards and reminders"
            ) { requestNotificationPermission() }
            rowsAdded++
        }

        if (rowsAdded == 0) {
            setupCard.visibility = View.GONE
        } else {
            setupCard.visibility = View.VISIBLE
            subtext.text = "Turn these on so the app can do its job:"
        }
    }

    private fun addPermissionRow(
        container: LinearLayout,
        title: String,
        subtitle: String,
        onClick: () -> Unit
    ) {
        val button = Button(this).apply {
            text = "$title\n$subtitle"
            textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
            setBackgroundResource(R.drawable.bg_button_primary)
            setTextColor(getColor(R.color.brand_text_primary))
            isAllCaps = false
            elevation = 0f
            textSize = 13f
            setOnClickListener { onClick() }
        }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 12 }
        button.layoutParams = params
        container.addView(button)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponent = "$packageName/${AppMonitorService::class.java.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledServices)
        while (splitter.hasNext()) {
            if (splitter.next().equals(expectedComponent, ignoreCase = true)) return true
        }
        return false
    }

    private fun areNotificationsGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // ---------- Live away-streak timer ----------

    private fun updateStreakDisplay() {
        val elapsedMillis = awayStreakTracker.elapsedMillis()
        val totalSeconds = elapsedMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        findViewById<TextView>(R.id.streakTimerText).text =
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

        val elapsedHours = awayStreakTracker.elapsedHours()
        val tier = AwayMilestones.tierForHours(elapsedHours)

        val iconView = findViewById<TextView>(R.id.streakTierIcon)
        val labelView = findViewById<TextView>(R.id.streakTierLabel)

        if (tier != null) {
            iconView.text = tier.emoji
            labelView.text = "${tier.name} tier — ${tier.hour}h+ away from social media"
        } else {
            iconView.text = "🕐"
            labelView.text = getString(R.string.streak_starting)
        }
        // Milestone notifications are handled by the background service
        // (AppMonitorService), so they fire even when this screen isn't open.
    }

    // ---------- AI Coach ----------

    private fun setupAiCoach() {
        findViewById<Button>(R.id.btnSaveApiKey).setOnClickListener {
            val key = findViewById<EditText>(R.id.apiKeyInput).text.toString().trim()
            if (key.isEmpty()) {
                Toast.makeText(this, "Enter your Gemini API key first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            apiKeyStore.saveKey(key)
            Toast.makeText(this, "API key saved on this device", Toast.LENGTH_SHORT).show()
            animateRobotBounce()
        }

        findViewById<Button>(R.id.btnAskAi).setOnClickListener {
            val key = apiKeyStore.getKey()
            if (key.isNullOrBlank()) {
                Toast.makeText(this, "Save your Gemini API key first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val responseView = findViewById<TextView>(R.id.aiResponseText)
            responseView.visibility = View.VISIBLE
            responseView.text = "Thinking…"
            animateRobotBounce()

            val hours = awayStreakTracker.elapsedHours()
            val tier = AwayMilestones.tierForHours(hours)
            val tierName = tier?.name ?: "Just started"

            GeminiApiClient.getMotivation(
                apiKey = key,
                streakHours = hours,
                tierName = tierName,
                onResult = { text ->
                    responseView.text = "🤖 $text"
                    animateRobotBounce()
                },
                onError = { error -> responseView.text = "Couldn't reach the AI: $error" }
            )
        }
    }

    /** A cute little bounce + wiggle for the robot mascot. */
    private fun animateRobotBounce() {
        val robot = findViewById<View>(R.id.robotMascot)
        robot.animate().cancel()
        robot.pivotX = robot.width / 2f
        robot.pivotY = robot.height.toFloat()

        robot.animate()
            .scaleX(1.25f).scaleY(1.25f).rotation(-8f)
            .setDuration(140)
            .withEndAction {
                robot.animate()
                    .scaleX(0.95f).scaleY(0.95f).rotation(8f)
                    .setDuration(140)
                    .withEndAction {
                        robot.animate()
                            .scaleX(1.05f).scaleY(1.05f).rotation(-4f)
                            .setDuration(110)
                            .withEndAction {
                                robot.animate()
                                    .scaleX(1f).scaleY(1f).rotation(0f)
                                    .setDuration(110)
                                    .start()
                            }.start()
                    }.start()
            }.start()
    }

    // ---------- Reminders ----------

    private fun setupReminders() {
        findViewById<Button>(R.id.btnOpenReminders).setOnClickListener {
            startActivity(Intent(this, RemindersActivity::class.java))
        }
    }

    // ---------- Books ----------

    private fun setupBookButtons() {
        val container = findViewById<LinearLayout>(R.id.booksContainer)
        container.removeAllViews()

        BookOpener.books.forEachIndexed { index, book ->
            val button = Button(this).apply {
                text = "📖  Read: ${book.displayName}"
                textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                setBackgroundResource(R.drawable.bg_button_secondary)
                setTextColor(getColor(R.color.brand_text_primary))
                isAllCaps = false
                elevation = 0f
                setOnClickListener { BookOpener.open(this@MainActivity, book) }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (index < BookOpener.books.size - 1) {
                params.bottomMargin = 12
            }
            button.layoutParams = params

            container.addView(button)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionRows()

        val overlayOk = Settings.canDrawOverlays(this)
        val accessibilityOk = isAccessibilityServiceEnabled()
        findViewById<TextView>(R.id.statusText).text =
            "Overlay: ${if (overlayOk) "✅" else "❌"}   " +
            "Accessibility: ${if (accessibilityOk) "✅" else "❌"}   " +
            "Notifications: ${if (areNotificationsGranted()) "✅" else "❌"}"

        streakHandler.post(streakTickRunnable)
    }

    override fun onPause() {
        super.onPause()
        streakHandler.removeCallbacks(streakTickRunnable)
    }
}
