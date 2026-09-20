package com.scrollbudget.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Reminder(
    val id: Int,
    val label: String,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true
)

/**
 * Simple JSON-backed store for user-created reminders. Keeps things
 * dependency-free (no Room/SQLite) since the list is expected to stay small.
 */
class ReminderStore(context: Context) {

    private val prefs = context.getSharedPreferences("scroll_budget_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_REMINDERS = "reminders_json"
        private const val KEY_NEXT_ID = "reminders_next_id"
    }

    fun getAll(): List<Reminder> {
        val json = prefs.getString(KEY_REMINDERS, null) ?: return emptyList()
        val array = JSONArray(json)
        val result = mutableListOf<Reminder>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                Reminder(
                    id = obj.getInt("id"),
                    label = obj.getString("label"),
                    hour = obj.getInt("hour"),
                    minute = obj.getInt("minute"),
                    enabled = obj.optBoolean("enabled", true)
                )
            )
        }
        return result
    }

    private fun saveAll(reminders: List<Reminder>) {
        val array = JSONArray()
        reminders.forEach { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("label", r.label)
            obj.put("hour", r.hour)
            obj.put("minute", r.minute)
            obj.put("enabled", r.enabled)
            array.put(obj)
        }
        prefs.edit().putString(KEY_REMINDERS, array.toString()).apply()
    }

    fun add(label: String, hour: Int, minute: Int): Reminder {
        val nextId = prefs.getInt(KEY_NEXT_ID, 1)
        prefs.edit().putInt(KEY_NEXT_ID, nextId + 1).apply()

        val reminder = Reminder(nextId, label, hour, minute, enabled = true)
        val updated = getAll() + reminder
        saveAll(updated)
        return reminder
    }

    fun remove(id: Int) {
        saveAll(getAll().filter { it.id != id })
    }

    fun getById(id: Int): Reminder? = getAll().find { it.id == id }
}
