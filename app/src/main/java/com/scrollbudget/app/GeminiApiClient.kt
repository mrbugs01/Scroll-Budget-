package com.scrollbudget.app

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Talks directly to Google's Gemini API using the user's own free API key
 * (from aistudio.google.com — genuinely free, no billing required for the
 * free tier). Plain HttpURLConnection on a background thread, result
 * delivered back on the main thread.
 */
object GeminiApiClient {

    private const val MODEL = "gemini-3.6-flash"
    private val mainHandler = Handler(Looper.getMainLooper())

    fun getMotivation(
        apiKey: String,
        streakHours: Int,
        tierName: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        Thread {
            try {
                val prompt = "I've been away from social media (Instagram/YouTube) for " +
                    "$streakHours hour(s) so far today, currently at the \"$tierName\" tier " +
                    "of a 9-tier reward system (Stone to Diamond). Write a short (2-3 " +
                    "sentences), warm, genuinely encouraging motivational message for me " +
                    "about staying off social media, specific to this amount of time. " +
                    "Do not use hashtags or emojis excessively — at most one."

                val requestBody = JSONObject().apply {
                    put("contents", JSONArray().put(
                        JSONObject().apply {
                            put("parts", JSONArray().put(
                                JSONObject().apply { put("text", prompt) }
                            ))
                        }
                    ))
                }

                val url = URL(
                    "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"
                )
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 15000
                connection.readTimeout = 20000

                OutputStreamWriter(connection.outputStream).use { it.write(requestBody.toString()) }

                val responseCode = connection.responseCode
                val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
                val responseText = stream.bufferedReader().use { it.readText() }

                if (responseCode in 200..299) {
                    val json = JSONObject(responseText)
                    val text = json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                    mainHandler.post { onResult(text.trim()) }
                } else {
                    val message = try {
                        JSONObject(responseText).getJSONObject("error").getString("message")
                    } catch (e: Exception) {
                        "API error ($responseCode)"
                    }
                    mainHandler.post { onError(message) }
                }
            } catch (e: Exception) {
                mainHandler.post { onError(e.message ?: "Network error") }
            }
        }.start()
    }
}
