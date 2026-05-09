package com.penguin.linguae.core.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.penguin.linguae.data.model.DailyReminderData
import com.penguin.linguae.data.model.NotificationSettingsData
import java.util.Calendar

object LearningReminderScheduler {
    const val CHANNEL_ID = "daily_learning_reminder"
    const val NOTIFICATION_ID = 2026

    private const val PREFS_NAME = "learning_reminder_settings"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_TIME = "time"
    private const val KEY_TIMEZONE = "timezone"
    private const val KEY_TITLE = "title"
    private const val KEY_MESSAGE = "message"
    private const val REQUEST_CODE = 20260509

    fun saveSettings(context: Context, settings: NotificationSettingsData) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, settings.notificationsEnabled && settings.pushNotifications && settings.dailyReminder.enabled)
            .putString(KEY_TIME, settings.reminderTime)
            .putString(KEY_TIMEZONE, settings.timezone)
            .putString(KEY_TITLE, settings.dailyReminder.title)
            .putString(KEY_MESSAGE, settings.dailyReminder.message)
            .apply()
    }

    fun getSavedSettings(context: Context): NotificationSettingsData {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(KEY_ENABLED, true)
        val reminderTime = prefs.getString(KEY_TIME, "20:00") ?: "20:00"
        val timezone = prefs.getString(KEY_TIMEZONE, "Asia/Ho_Chi_Minh") ?: "Asia/Ho_Chi_Minh"
        val title = prefs.getString(KEY_TITLE, "Đến giờ luyện tập rồi!") ?: "Đến giờ luyện tập rồi!"
        val message = prefs.getString(KEY_MESSAGE, "Vào Linguae làm nhiệm vụ hằng ngày nhé.")

        return NotificationSettingsData(
            notificationsEnabled = enabled,
            pushNotifications = enabled,
            reminderTime = reminderTime,
            timezone = timezone,
            dailyReminder = DailyReminderData(
                enabled = enabled,
                title = title,
                message = message,
            )
        )
    }

    fun applySchedule(context: Context, settings: NotificationSettingsData) {
        ensureChannel(context)

        val enabled = settings.notificationsEnabled &&
            settings.pushNotifications &&
            settings.dailyReminder.enabled

        if (!enabled) {
            cancel(context)
            return
        }

        scheduleDaily(context, settings.reminderTime)
    }

    fun rescheduleFromStorage(context: Context) {
        applySchedule(context, getSavedSettings(context))
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context))
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Nhắc học hằng ngày",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Thông báo nhắc người dùng vào app luyện tập mỗi ngày"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleDaily(context: Context, reminderTime: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(reminderTime),
            AlarmManager.INTERVAL_DAY,
            pendingIntent(context),
        )
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, LearningReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun nextTriggerMillis(reminderTime: String): Long {
        val parts = reminderTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 20
        val minute = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }
}
