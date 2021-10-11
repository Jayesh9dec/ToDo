package com.example.todo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ToDo(
    var title:String,
    var detail:String,
    var type:String,
    var date:Long,
    var time:Long,
    var done:Int= 0,
    @PrimaryKey(autoGenerate = true)
    var id:Long=0L
)
