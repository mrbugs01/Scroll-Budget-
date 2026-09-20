package com.scrollbudget.app

import android.content.Context

/**
 * Stores the user's own Gemini API key locally on-device (SharedPreferences,
 * plain text). This is NOT sent anywhere except directly to
 * generativelanguage.googleapis.com from the user's own phone. Note: for
 * stronger protection consider EncryptedSharedPreferences (androidx.security)
 * in a future version.
 */
class ApiKeyStore(context: Context) {

    private val prefs = context.getSharedPreferences("scroll_budget_prefs", Context.MODE_PRIVATE)

    fun getKey(): String? = prefs.getString("gemini_api_key", null)

    fun saveKey(key: String) {
        prefs.edit().putString("gemini_api_key", key.trim()).apply()
    }

    fun hasKey(): Boolean = !getKey().isNullOrBlank()
}
