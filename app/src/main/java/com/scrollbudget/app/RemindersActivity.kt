package com.scrollbudget.app

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class RemindersActivity : AppCompatActivity() {

    private lateinit var store: ReminderStore
    private var pickedHour: Int = 8
    private var pickedMinute: Int = 0
    private var timePicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        store = ReminderStore(this)

        val labelInput = findViewById<EditText>(R.id.reminderLabelInput)
        val pickTimeButton = findViewById<Button>(R.id.btnPickTime)

        pickTimeButton.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    pickedHour = hour
                    pickedMinute = minute
                    timePicked = true
                    pickTimeButton.text = "🕐  ${formatTime(hour, minute)}"
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
            ).show()
        }

        findViewById<Button>(R.id.btnAddReminder).setOnClickListener {
            val label = labelInput.text.toString().trim()
            if (label.isEmpty()) {
                Toast.makeText(this, "Please enter what this reminder is for", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!timePicked) {
                Toast.makeText(this, "Please pick a time first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reminder = store.add(label, pickedHour, pickedMinute)
            ReminderScheduler.schedule(this, reminder)

            labelInput.text.clear()
            pickTimeButton.text = "🕐  Pick time"
            timePicked = false

            refreshList()
            Toast.makeText(this, "Reminder added", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        refreshList()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return String.format(Locale.US, "%02d:%02d %s", displayHour, minute, amPm)
    }

    private fun refreshList() {
        val container = findViewById<LinearLayout>(R.id.remindersContainer)
        val emptyState = findViewById<TextView>(R.id.emptyStateText)
        container.removeAllViews()

        val reminders = store.getAll().sortedBy { it.hour * 60 + it.minute }

        emptyState.visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE

        val inflater = LayoutInflater.from(this)
        reminders.forEach { reminder ->
            val row = inflater.inflate(R.layout.item_reminder, container, false)
            row.findViewById<TextView>(R.id.rowTime).text = formatTime(reminder.hour, reminder.minute)
            row.findViewById<TextView>(R.id.rowLabel).text = reminder.label
            row.findViewById<Button>(R.id.rowDeleteButton).setOnClickListener {
                ReminderScheduler.cancel(this, reminder.id)
                store.remove(reminder.id)
                refreshList()
            }
            container.addView(row)
        }
    }
}
