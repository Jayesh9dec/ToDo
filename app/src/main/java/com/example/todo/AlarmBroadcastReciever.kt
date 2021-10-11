package com.example.todo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

class AlarmBroadcastReciever: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent !=null && intent.action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            startRescheduledAlarm(context)
        }
        else if(intent!=null)
        {
            startAlarmService(context,intent)
        }
    }
    private fun startRescheduledAlarm(context: Context?) {

    }
    private fun startAlarmService(context: Context?, intent: Intent) {
        val intentservice=Intent(context,AlarmService::class.java)
        intentservice.putExtra("ID",intent.getStringExtra("ID"))
        intentservice.putExtra("TITLE",intent.getStringExtra("TITLE"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intentservice)
        }
        else{
            context?.startService(intentservice)
        }
    }
}