package id.dipoengoro.stopwatch.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*

class StopwatchService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getLongExtra(CURRENT_TIME, 0L)
        timer.scheduleAtFixedRate(StopwatchTimerTask(time), 0, 1)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    companion object {
        const val CURRENT_TIME = "current time"
        const val UPDATED_TIME = "updated time"
    }

    private inner class StopwatchTimerTask(private var time: Long): TimerTask() {
        override fun run() {
            val intent = Intent(UPDATED_TIME)
            time++
            intent.putExtra(CURRENT_TIME, time)
            sendBroadcast(intent)
        }
    }
}