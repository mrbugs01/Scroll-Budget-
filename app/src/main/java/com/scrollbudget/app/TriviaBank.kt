package com.scrollbudget.app

import kotlin.random.Random

/**
 * Text-based general knowledge trivia — famous paintings, historical figures,
 * monuments. All public-domain facts, no copyrighted images/logos needed.
 *
 * Each question has 1 correct answer and 2 wrong ones. The 3 options are
 * shuffled into random A/B/C positions every time, so the correct answer
 * isn't always in the same spot.
 */
object TriviaBank {

    private data class Q(val question: String, val correct: String, val wrong: List<String>)

    private val questions = listOf(
        Q("Who painted the Mona Lisa?",
            "Leonardo da Vinci", listOf("Pablo Picasso", "Vincent van Gogh")),
        Q("Which museum is the Mona Lisa in?",
            "Louvre, Paris", listOf("British Museum, London", "MET, New York")),
        Q("Alexander the Great was king of which kingdom?",
            "Macedonia", listOf("Rome", "Persia")),
        Q("Who was Alexander the Great's teacher?",
            "Aristotle", listOf("Socrates", "Pythagoras")),
        Q("Who built the Taj Mahal?",
            "Shah Jahan", listOf("Akbar", "Aurangzeb")),
        Q("Which city is the Taj Mahal in?",
            "Agra", listOf("Delhi", "Jaipur")),
        Q("Which country is the Great Wall of China in?",
            "China", listOf("Japan", "Mongolia")),
        Q("Which country are the Pyramids of Giza in?",
            "Egypt", listOf("Sudan", "Mexico")),
        Q("Which city is the Eiffel Tower in?",
            "Paris", listOf("London", "Rome")),
        Q("Which country is the Statue of Liberty in?",
            "USA", listOf("France", "UK")),
        Q("Who painted The Starry Night?",
            "Vincent van Gogh", listOf("Claude Monet", "Salvador Dali")),
        Q("Where was Mahatma Gandhi born?",
            "Porbandar, Gujarat", listOf("Mumbai", "Delhi")),
        Q("Who was India's first Prime Minister?",
            "Jawaharlal Nehru", listOf("Sardar Patel", "Rajendra Prasad")),
        Q("Cleopatra was the queen of which country?",
            "Egypt", listOf("Greece", "Rome")),
        Q("Julius Caesar was a leader of which empire?",
            "Roman Empire", listOf("Greek Empire", "Persian Empire")),
        Q("Which city is the Leaning Tower in?",
            "Pisa, Italy", listOf("Rome, Italy", "Venice, Italy")),
        Q("Which city is the Colosseum in?",
            "Rome", listOf("Athens", "Paris")),
        Q("Who sculpted The Thinker?",
            "Auguste Rodin", listOf("Michelangelo", "Donatello")),
        Q("What is Albert Einstein famous for?",
            "Theory of Relativity", listOf("Theory of Evolution", "Big Bang Theory")),
        Q("What is Isaac Newton famous for?",
            "Laws of Motion & Gravity", listOf("Theory of Relativity", "Periodic Table")),
        Q("APJ Abdul Kalam was famous in which field?",
            "Aerospace Scientist", listOf("Freedom Fighter", "Poet")),
        Q("Which country is Machu Picchu in?",
            "Peru", listOf("Brazil", "Mexico")),
        Q("Which city is Big Ben in?",
            "London", listOf("Paris", "New York")),
        Q("Who painted The Last Supper?",
            "Leonardo da Vinci", listOf("Raphael", "Michelangelo")),
        Q("Nelson Mandela was a leader of which country?",
            "South Africa", listOf("Kenya", "Nigeria")),
        Q("Who painted the Sistine Chapel ceiling?",
            "Michelangelo", listOf("Leonardo da Vinci", "Raphael")),
        Q("Which city is the Christ the Redeemer statue in?",
            "Rio de Janeiro", listOf("Lisbon", "Mexico City")),
        Q("Rani Lakshmibai was the queen of which state?",
            "Jhansi", listOf("Gwalior", "Indore")),
        Q("Which army did Subhas Chandra Bose form?",
            "Azad Hind Fauj", listOf("Bharatiya Sena", "Swaraj Dal")),
        Q("Who painted The Scream?",
            "Edvard Munch", listOf("Pablo Picasso", "Salvador Dali"))
    )

    fun random(): PuzzleItem.Trivia {
        val q = questions[Random.nextInt(questions.size)]
        val options = (listOf(q.correct) + q.wrong).shuffled()
        val correctIndex = options.indexOf(q.correct)
        return PuzzleItem.Trivia(q.question, options, correctIndex)
    }
}
