package com.penguin.linguae.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            LearningReminderScheduler.rescheduleFromStorage(context)
        }
    }
}
