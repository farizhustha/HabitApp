package com.dicoding.habitapp.ui.countdown

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK

class CountDownActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        workManager = WorkManager.getInstance(this)

        val habit = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(HABIT, Habit::class.java) as Habit
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Habit>(HABIT) as Habit
        }

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

        val viewModel = ViewModelProvider(this)[CountDownViewModel::class.java]

        //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
        viewModel.setInitialTime(habit.minutesFocus)
        viewModel.currentTimeString.observe(this) { time ->
            findViewById<TextView>(R.id.tv_count_down).text = time
        }

        //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
        viewModel.eventCountDownFinish.observe(this) { state ->
            updateButtonState(!state)
            if (state) {
                startOneTimeTask(habit)
            } else {
                workManager.cancelUniqueWork(NOTIF_UNIQUE_WORK)
            }
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            viewModel.startTimer()
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            viewModel.resetTimer()
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }

    private fun startOneTimeTask(habit: Habit) {
        val data = Data.Builder().apply {
            putInt(HABIT_ID, habit.id)
            putString(HABIT_TITLE, habit.title)
        }.build()

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            NOTIF_UNIQUE_WORK,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }
}