package com.scrollbudget.app

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlin.random.Random

/**
 * Draws a full-screen overlay on top of the current app (requires the
 * SYSTEM_ALERT_WINDOW / "Display over other apps" permission).
 *
 * Two states:
 *  - Puzzle screen: solve a simple sum to unlock EXTENSION_MILLIS more time
 *  - Final screen: extensions used up for today, just a message + close
 */
class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null

    private fun windowType() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

    fun isShowing(): Boolean = overlayView != null

    fun showPuzzle(appName: String, onSolved: () -> Unit, onDismiss: () -> Unit) {
        if (overlayView != null) return

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.overlay_puzzle, null)

        val icon = view.findViewById<TextView>(R.id.overlayIcon)
        val message = view.findViewById<TextView>(R.id.overlayMessage)
        val input = view.findViewById<EditText>(R.id.puzzleInput)
        val choicesContainer = view.findViewById<View>(R.id.choicesContainer)
        val choiceButtons = listOf(
            view.findViewById<Button>(R.id.choice0),
            view.findViewById<Button>(R.id.choice1),
            view.findViewById<Button>(R.id.choice2)
        )
        val choiceLabels = listOf("A", "B", "C")
        val submitBtn = view.findViewById<Button>(R.id.submitButton)
        val closeBtn = view.findViewById<Button>(R.id.closeButton)

        icon.text = "⏳"

        fun renderPuzzle(puzzle: PuzzleItem, prefix: String) {
            when (puzzle) {
                is PuzzleItem.Math -> {
                    input.visibility = View.VISIBLE
                    submitBtn.visibility = View.VISIBLE
                    choicesContainer.visibility = View.GONE
                    input.text.clear()
                    message.text = "$prefix${puzzle.question}"
                }
                is PuzzleItem.Trivia -> {
                    input.visibility = View.GONE
                    submitBtn.visibility = View.GONE
                    choicesContainer.visibility = View.VISIBLE
                    message.text = "$prefix${puzzle.question}"
                    puzzle.choices.forEachIndexed { i, choiceText ->
                        choiceButtons[i].text = "${choiceLabels[i]})  $choiceText"
                    }
                }
            }
        }

        var puzzle = PuzzleGenerator.generate()
        renderPuzzle(puzzle, "You've hit today's budget for $appName.\nWant 10 more minutes? Solve this:\n\n")

        fun onWrong() {
            puzzle = PuzzleGenerator.generate()
            renderPuzzle(puzzle, "Wrong answer, try a new one:\n\n")
        }

        submitBtn.setOnClickListener {
            val current = puzzle
            if (current is PuzzleItem.Math) {
                val userAnswer = input.text.toString().toIntOrNull()
                if (userAnswer == current.answer) {
                    remove()
                    onSolved()
                } else {
                    onWrong()
                }
            }
        }

        choiceButtons.forEachIndexed { i, btn ->
            btn.setOnClickListener {
                val current = puzzle
                if (current is PuzzleItem.Trivia) {
                    if (i == current.correctIndex) {
                        remove()
                        onSolved()
                    } else {
                        onWrong()
                    }
                }
            }
        }

        closeBtn.setOnClickListener {
            remove()
            onDismiss()
        }

        addOverlay(view)
    }

    fun showFinalMessage(appName: String, usedMinutes: Long, onDismiss: () -> Unit) {
        if (overlayView != null) return

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.overlay_puzzle, null)

        val templates = context.resources.getStringArray(R.array.final_messages)
        val template = templates[Random.nextInt(templates.size)]

        view.findViewById<TextView>(R.id.overlayIcon).text = "🌙"
        view.findViewById<TextView>(R.id.overlayMessage).text =
            String.format(template, appName, usedMinutes)

        view.findViewById<EditText>(R.id.puzzleInput).visibility = View.GONE
        view.findViewById<Button>(R.id.submitButton).visibility = View.GONE
        view.findViewById<View>(R.id.choicesContainer).visibility = View.GONE

        view.findViewById<Button>(R.id.closeButton).apply {
            text = "Got it"
            setOnClickListener {
                remove()
                onDismiss()
            }
        }

        addOverlay(view)
    }

    private fun addOverlay(view: View) {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            windowType(),
            0, // focusable, so the EditText for puzzle input works
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        overlayView = view
        windowManager.addView(view, params)
    }

    fun remove() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
