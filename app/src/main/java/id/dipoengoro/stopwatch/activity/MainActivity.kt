package id.dipoengoro.stopwatch.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import id.dipoengoro.stopwatch.R
import id.dipoengoro.stopwatch.databinding.ActivityMainBinding
import id.dipoengoro.stopwatch.service.StopwatchService
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MyTag"
        private fun log(string: String) {
            Log.i(TAG, string)
        }
    }

    private lateinit var binding: ActivityMainBinding
    private var isStart = false
    private var isReset = false
    private lateinit var serviceIntent: Intent
    private var time = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            binding.textTime.text = getFormattedTime(time)

            buttonStartPause.setOnClickListener {
                onStartPauseClicked()
                if (isStart) start() else pause()
            }

            buttonResetLap.setOnClickListener {
                if (isStart) lap() else reset()
            }
        }

        serviceIntent = Intent(applicationContext, StopwatchService::class.java)
        registerReceiver(updateTime, IntentFilter(StopwatchService.UPDATED_TIME))
    }

    private fun onStartPauseClicked() {
        isStart = !isStart
        binding.apply {
            if (isStart) {
                buttonStartPause.text = getString(R.string.pause)
                buttonResetLap.text = getString(R.string.lap)
            } else {
                stopService(serviceIntent)
                buttonStartPause.text = getString(R.string.start)
                buttonResetLap.text = getString(R.string.reset)
            }
        }
    }

    private fun start() {
        serviceIntent.putExtra(StopwatchService.CURRENT_TIME, time)
        startService(serviceIntent)
    }

    private fun pause() {
        stopService(serviceIntent)
    }

    private fun reset() {
        pause()
        time = 0
        binding.textTime.text = getFormattedTime(time)
    }

    private fun lap() {
        Toast.makeText(this, "Lap Clicked", Toast.LENGTH_SHORT).show()
    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            time = intent.getLongExtra(StopwatchService.CURRENT_TIME, 0L)
            log("Time: $time")
            binding.textTime.text = getFormattedTime(time)
        }
    }

    private fun getFormattedTime(millisecond: Long): String {
        val hour = TimeUnit.MILLISECONDS.toHours(millisecond)
        val minute = TimeUnit.MILLISECONDS.toMinutes(millisecond) % 60
        val second = TimeUnit.MILLISECONDS.toSeconds(millisecond) % 60
        val milli = millis(millisecond)
        log("milli: $milli")
        log("second: $second")
        log("minute: $minute")
        log("hour: $hour")
//        log("milli size: $size")
        return getString(R.string.template_time).format(hour, minute, second, milli)
//        return ""
    }

    private fun millis(millisecond: Long): String {
        when {
            millisecond < 10 -> {
                return ".00$millisecond"
            }
            millisecond < 100 -> {
                return ".0$millisecond"
            }
            millisecond < 1000 -> {
                return ".$millisecond"
            }
            else -> {
                val millis = millisecond.toString()
                return ".${millis.takeLast(3)}"
            }
        }
    }
}