package com.dicoding.habitapp.utils

import java.util.concurrent.Executors

const val HABIT = "HABIT"
const val HABIT_ID = "HABIT_ID"
const val HABIT_TITLE = "HABIT_TITLE"
const val NOTIFICATION_CHANNEL_ID = "notify-channel"
const val NOTIF_UNIQUE_WORK: String = "NOTIF_UNIQUE_WORK"

private val SINGLE_EXECUTOR = Executors.newSingleThreadExecutor()

fun executeThread(f: () -> Unit) {
    SINGLE_EXECUTOR.execute(f)
}

