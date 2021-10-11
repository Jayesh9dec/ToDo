package com.example.todo

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val todolist= arrayListOf<ToDo>()
    val adapter= TodoAdapter(todolist)
    val db by lazy {
        AppDB.getDatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv.layoutManager=LinearLayoutManager(this)
        rv.adapter=adapter
        swipeFun()
        createNotificationChannnel();
        db.todoDao().getTasksList().observe(this , {
            if(!it.isNullOrEmpty())
            {
                todolist.clear()
                todolist.addAll(it)
                adapter.notifyDataSetChanged()
                if(emptytv.visibility!=View.GONE)
                    emptytv.visibility=View.GONE
            }
            else
            {
                todolist.clear()
                adapter.notifyDataSetChanged()
                if(adapter.itemCount==0)
                    emptytv.visibility=View.VISIBLE
            }
        })
        newTaskbtn.setOnClickListener {
            val x=Intent(this,NewTask::class.java)
            startActivity(x)
        }
    }

    private fun createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "TODO_APP_CHANNEL",
                "TodoAppChannel",
                NotificationManager.IMPORTANCE_HIGH
            )
            serviceChannel.enableLights(true)
            serviceChannel.enableVibration(true)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun swipeFun()
    {
        val todotouchcallback= object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean=false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction==ItemTouchHelper.RIGHT)
                {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().taskDone(adapter.getItemId(viewHolder.absoluteAdapterPosition))
                    }
                    GlobalScope.launch(Dispatchers.IO) {
                        val am= applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
                        val intent=Intent(applicationContext,AlarmBroadcastReciever::class.java)
                        val id=adapter.getItemId(viewHolder.absoluteAdapterPosition)
                        val todo=db.todoDao().getByID(id)
                        intent.putExtra("ID",id.toString())
                        intent.putExtra("Title",todo.title)
                        val pi = PendingIntent.getBroadcast(applicationContext,101,intent, PendingIntent.FLAG_NO_CREATE)
                        if(pi!=null)
                            am.cancel(pi)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemview=viewHolder.itemView
                if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE)
                {
                    val paint= Paint()
                    val icon:Bitmap
                    if(dX>0)
                    {
                        icon=BitmapFactory.decodeResource(resources,R.drawable.ic_check_icon_png)
                        paint.color= Color.parseColor("#00FF00")
                        c.drawRect(itemview.left.toFloat(),itemview.top.toFloat(),
                        itemview.left.toFloat()+dX,itemview.bottom.toFloat(),paint)
                        c.drawBitmap(
                            icon,
                            itemview.left.toFloat(),
                        itemview.top.toFloat()+(itemview.bottom.toFloat()-itemview.top.toFloat()-icon.height.toFloat())/2,
                            null)
                        viewHolder.itemView.translationX=dX
                    }
                }
                else
                {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }
        val todotouchhelper=ItemTouchHelper(todotouchcallback)
        todotouchhelper.attachToRecyclerView(rv)
    }

    fun startTasksHistory(item: MenuItem) {
        val x=Intent(this,TasksHistory::class.java)
        startActivity(x)
    }
    fun searchWidgetFun(item: MenuItem){
        val searchView = item.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                displaySearchedItems()
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                displaySearchedItems()
                return true
            }
        })

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty())
                {
                    displaySearchedItems(newText)
                }
                return true
            }
        })
    }
    fun displaySearchedItems(newtext: String ="")
    {
        db.todoDao().getTasksList().observe(this, Observer {
            if(!it.isNullOrEmpty())
            {
                todolist.clear()
                todolist.addAll( it.filter { todo->
                        todo.title.contains(newtext,true)
                })
                adapter.notifyDataSetChanged()
            }
        })
     }
}
