package com.example.todo

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    private lateinit var mp:MediaPlayer
    private lateinit var vibe:Vibrator
    override fun onCreate() {
        super.onCreate()
        mp = MediaPlayer.create(this,R.raw.todoringtone)
        mp.isLooping=true
        vibe= getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent==null)
            return super.onStartCommand(intent, flags, startId)

        val alarmIntent = Intent(applicationContext,AlarmActivity::class.java)
        val id= intent.getStringExtra("ID")!!
        alarmIntent.putExtra("ID",id)
        val title = intent.getStringExtra("TITLE")
        mp.start()
        val pattern = longArrayOf(0,100,1000)
        vibe.vibrate(pattern,0)
        val pi= PendingIntent.getActivity(applicationContext,101,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification= NotificationCompat.Builder(this,"TODO_APP_CHANNEL")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setContentTitle(title)
            .setContentText("Your task is ready")
            .setSmallIcon(R.drawable.ic_alarm_foreground)
            .setContentIntent(pi)
            .build()
        startForeground(1,notification)
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        mp.stop()
        vibe.cancel()
    }
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}