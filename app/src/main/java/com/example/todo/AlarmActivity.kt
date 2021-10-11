package com.example.todo

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alarm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AlarmActivity : AppCompatActivity() {

    private val db by lazy {
        AppDB.getDatabase(this)
    }
    private var id:Long =0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        id=intent!!.getStringExtra("ID")!!.toLong()
        GlobalScope.launch (Dispatchers.IO){
            val todo = db.todoDao().getByID(id)
            textView.text=todo.title
        }
        animateClock()
        complete.setOnClickListener {
            val intentservice = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentservice)
            GlobalScope.launch(Dispatchers.IO) {
                db.todoDao().taskDone(id)
            }
            finish()
        }
        snooze.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MINUTE, 2)

            val am= applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent= Intent(applicationContext,AlarmBroadcastReciever::class.java)
            intent.putExtra("ID",id.toString())
            intent.putExtra("TITLE",textView.text)
            val pi = PendingIntent.getBroadcast(applicationContext,101,intent, PendingIntent.FLAG_UPDATE_CURRENT)
            am.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pi)
            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)
            finish()
        }
    }

    private fun animateClock() {
        val rotateAnimation = ObjectAnimator.ofFloat(clock, "rotation", 0f, 20f, 0f, -20f, 0f)
        rotateAnimation.repeatCount = ValueAnimator.INFINITE
        rotateAnimation.duration = 800
        rotateAnimation.start()
    }
}