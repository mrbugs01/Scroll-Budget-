package com.scrollbudget.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object MilestoneNotifier {

    private const val CHANNEL_ID = "away_milestones_channel"

    fun notify(context: Context, tier: AwayMilestones.Milestone) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Away-Streak Rewards",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies you when you unlock a new away-from-social-media reward"
            }
            manager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
            context, tier.hour, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle("${tier.emoji} ${tier.title}")
            .setContentText(tier.detail.lineSequence().first())
            .setStyle(NotificationCompat.BigTextStyle().bigText(tier.detail))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            NotificationManagerCompat.from(context).notify(2000 + tier.hour, notification)
        }
    }
}
