package com.scrollbudget.app

import kotlin.random.Random

/**
 * A puzzle the user must solve to extend their time budget.
 * Two kinds:
 *  - Math: a text answer typed as a number
 *  - Trivia: a multiple-choice question (famous art, history, landmarks — all
 *    public-domain general knowledge, no copyrighted logos/photos involved)
 */
sealed class PuzzleItem {
    abstract val question: String

    data class Math(override val question: String, val answer: Int) : PuzzleItem()

    data class Trivia(
        override val question: String,
        val choices: List<String>,
        val correctIndex: Int
    ) : PuzzleItem()
}

object PuzzleGenerator {

    fun generate(): PuzzleItem {
        // 1 in 3 chance of trivia, otherwise a math puzzle — keeps math as the
        // primary "friction" mechanic while trivia adds variety.
        return if (Random.nextInt(3) == 0) {
            TriviaBank.random()
        } else {
            when (Random.nextInt(4)) {
                0 -> addition()
                1 -> subtraction()
                2 -> multiplication()
                else -> sequence()
            }
        }
    }

    private fun addition(): PuzzleItem.Math {
        val a = Random.nextInt(5, 45)
        val b = Random.nextInt(5, 45)
        return PuzzleItem.Math("$a + $b = ?", a + b)
    }

    private fun subtraction(): PuzzleItem.Math {
        val a = Random.nextInt(20, 60)
        val b = Random.nextInt(1, a) // keep result positive
        return PuzzleItem.Math("$a − $b = ?", a - b)
    }

    private fun multiplication(): PuzzleItem.Math {
        val a = Random.nextInt(2, 12)
        val b = Random.nextInt(2, 12)
        return PuzzleItem.Math("$a × $b = ?", a * b)
    }

    private fun sequence(): PuzzleItem.Math {
        val start = Random.nextInt(1, 10)
        val step = Random.nextInt(2, 8)
        val n1 = start
        val n2 = start + step
        val n3 = start + step * 2
        val n4 = start + step * 3 // this is the answer
        return PuzzleItem.Math("$n1, $n2, $n3, ? (what's the next number)", n4)
    }
}
