package com.scrollbudget.app

/**
 * Tiered rewards for staying away from monitored apps (Instagram/YouTube)
 * continuously. Each milestone has an icon shown above the live timer and
 * a detailed message explaining the mental-health benefit of reaching it.
 */
object AwayMilestones {

    data class Milestone(
        val hour: Int,
        val name: String,
        val emoji: String,
        val title: String,
        val detail: String
    )

    val tiers = listOf(
        Milestone(
            hour = 1,
            name = "Stone",
            emoji = "🪨",
            title = "1 Hour Away — Stone Unlocked",
            detail = "One full hour without scrolling.\n\n" +
                "In this one hour, the artificial dopamine spike-crash cycle " +
                "that feeds trigger every 10-15 seconds never got started. " +
                "Your brain got a chance to find satisfaction in real things " +
                "instead — something scrolling quietly steals from you."
        ),
        Milestone(
            hour = 2,
            name = "Silver",
            emoji = "🥈",
            title = "2 Hours Away — Silver Unlocked",
            detail = "2 hours in.\n\n" +
                "If you'd been scrolling, your brain would have chased 150-200 " +
                "quick rewards by now (every swipe is a tiny dopamine hit). " +
                "Instead, your focus is slowly resetting — anxiety and " +
                "restlessness start to ease off around this point."
        ),
        Milestone(
            hour = 3,
            name = "Gold",
            emoji = "🥇",
            title = "3 Hours Away — Gold Unlocked",
            detail = "3-hour streak — Gold level!\n\n" +
                "Research shows that 3 hours of continuous scroll-free time " +
                "lets cortisol (your stress hormone) start dropping. The " +
                "negativity and comparison that doom-scrolling brings hasn't " +
                "touched you at all this whole time."
        ),
        Milestone(
            hour = 4,
            name = "Emerald",
            emoji = "🟢",
            title = "4 Hours Away — Emerald Unlocked",
            detail = "4 hours — Emerald tier.\n\n" +
                "That much scrolling would have left your eyes tired, your " +
                "neck and posture strained, and your attention span shorter. " +
                "Instead, your mind is now more capable of deep focus — " +
                "studying, working, or creative thinking."
        ),
        Milestone(
            hour = 5,
            name = "Sapphire",
            emoji = "🔵",
            title = "5 Hours Away — Sapphire Unlocked",
            detail = "5-hour streak — Sapphire!\n\n" +
                "That long a scroll session, the average user sees 300+ " +
                "posts/reels — most of it unrealistic lifestyle or body " +
                "comparisons that chip away at self-esteem, one small hit at " +
                "a time. You skipped all of it."
        ),
        Milestone(
            hour = 6,
            name = "Ruby",
            emoji = "🔴",
            title = "6 Hours Away — Ruby Unlocked",
            detail = "6 hours — Ruby tier!\n\n" +
                "That much screen exposure would suppress melatonin (your " +
                "sleep hormone) if it were evening. You've given your brain a " +
                "long, uninterrupted stretch to think, rest, or actually get " +
                "something real done."
        ),
        Milestone(
            hour = 7,
            name = "Topaz",
            emoji = "🟠",
            title = "7 Hours Away — Topaz Unlocked",
            detail = "7-hour streak — Topaz!\n\n" +
                "That's basically a full productive workday's worth of time. " +
                "You got that time back — whether you thought, worked, or " +
                "just rested, all of it is better for your brain than " +
                "scrolling would have been."
        ),
        Milestone(
            hour = 8,
            name = "Amethyst",
            emoji = "🟣",
            title = "8 Hours Away — Amethyst Unlocked",
            detail = "8 hours — Amethyst tier!\n\n" +
                "That much continuous scrolling would have tightened the " +
                "addiction loop even further — the more you scroll, the more " +
                "you crave scrolling. You broke that loop instead. Your " +
                "dopamine baseline is naturally resetting."
        ),
        Milestone(
            hour = 9,
            name = "Diamond",
            emoji = "💎",
            title = "9 Hours Away — Diamond Unlocked! 🎉",
            detail = "9 hours — a full Diamond streak complete!\n\n" +
                "Here's the harm you avoided today just by staying away:\n" +
                "• Anxiety and comparison-triggered low mood, avoided\n" +
                "• Attention span and deep-focus capacity, protected\n" +
                "• Sleep-disrupting screen exposure, reduced\n" +
                "• Your dopamine system got the chance to reset naturally\n" +
                "• That time went into real life, work, or people instead\n\n" +
                "This isn't a small thing — it's discipline, and it will " +
                "genuinely show up in how you feel."
        )
    )

    fun tierForHours(hours: Int): Milestone? =
        tiers.lastOrNull { it.hour <= hours }
}
