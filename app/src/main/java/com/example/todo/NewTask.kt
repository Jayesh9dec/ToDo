package com.example.todo

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


const val DB_NAME="TODO.db"
class NewTask : AppCompatActivity(), View.OnClickListener {

    private lateinit var cal:Calendar
    private lateinit var datelistener:DatePickerDialog.OnDateSetListener
    private lateinit var timelistener:TimePickerDialog.OnTimeSetListener

    //Fri, 2 Aug 2021
    private val date_string="EEE, d MMM yyyy"
    private val time_string="h:mm a"


    private val types_of_task= arrayListOf("Personal","Business","Shopping","Exercise","Medicine","Groceries","Other")

    private var finalDate:Long=0L
    private var finalTime:Long=0L
    private val db by lazy {
        AppDB.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        dateEdit.setOnClickListener(this)
        timeEdit.setOnClickListener(this)
        setUpSpinner()
        savebtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        when(v.id)
        {
            R.id.dateEdit->{
                setDate()
            }
            R.id.timeEdit->{
                setTime()
            }
            R.id.savebtn->{
                saveToDo()
            }
        }
    }

    private fun saveToDo() {

        if(finalDate==0L && finalTime==0L)
        {
            Toast.makeText(this, "Select Date and Time", Toast.LENGTH_SHORT).show()
            return
        }
        else if(finalDate==0L)
        {
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show()
            return
        }
        else if(finalTime==0L)
        {
            Toast.makeText(this, "Select Time", Toast.LENGTH_SHORT).show()
            return
        }
        val cal = Calendar.getInstance()
        if (finalTime < cal.timeInMillis) {
            Toast.makeText(this, "Select Future Time", Toast.LENGTH_SHORT).show()
            return
        }
        val task = ToDo(
            taskTitle.text.toString(),
            taskDetail.text.toString(),
            tasktypespinner.selectedItem.toString(),
            finalDate,
            finalTime,
            0
        )
        GlobalScope.launch(Dispatchers.IO) {
            val id=db.todoDao().insert(task)
            val am= applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            val intent= Intent(applicationContext,AlarmBroadcastReciever::class.java)
            intent.putExtra("ID",id.toString())
            intent.putExtra("TITLE",task.title)
            val pi = PendingIntent.getBroadcast(applicationContext,101,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            am.setExact(AlarmManager.RTC_WAKEUP,task.time,pi)
        }
        Toast.makeText(this,"Task Saved",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setDate() {
        cal= Calendar.getInstance()
        datelistener=DatePickerDialog.OnDateSetListener{_:DatePicker,year:Int,month:Int,dayOfMonth:Int->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }

        val datepickerdialog=DatePickerDialog(
            this,
            datelistener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))

        datepickerdialog.datePicker.minDate=System.currentTimeMillis()
        datepickerdialog.show()
    }

    private fun setTime() {
        cal.timeInMillis=finalDate
        timelistener=TimePickerDialog.OnTimeSetListener{_:TimePicker,hourOfDay:Int,minute:Int->
            cal.set(Calendar.HOUR_OF_DAY,hourOfDay)
            cal.set(Calendar.MINUTE,minute)
            cal.set(Calendar.SECOND,0)
            updateTime()
        }
        val timePickerDialog=TimePickerDialog(
            this,
            timelistener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun setUpSpinner() {
        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,types_of_task)
        tasktypespinner.adapter=adapter
    }

    private fun updateDate() {
        val sdf= SimpleDateFormat(date_string)
        dateEdit.setText(sdf.format(cal.time))
        timeLayout.visibility=View.VISIBLE
        finalDate=cal.timeInMillis
    }

    private fun updateTime() {
        val sdf= SimpleDateFormat(time_string)
        timeEdit.setText(sdf.format(cal.time))
        finalTime=cal.timeInMillis
    }
}