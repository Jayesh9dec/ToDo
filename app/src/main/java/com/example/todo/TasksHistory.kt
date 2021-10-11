package com.example.todo

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_tasks_history.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TasksHistory : AppCompatActivity() {

    private val todolist= arrayListOf<ToDo>()
    val adapter= TodoAdapter(todolist)
    val db by lazy {
        AppDB.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_history)

        rvhistory.layoutManager=LinearLayoutManager(this)
        rvhistory.adapter=adapter
        swipeFun()
        db.todoDao().getDoneTasks().observe(this, Observer {
            if(!it.isNullOrEmpty())
            {
                todolist.clear()
                todolist.addAll(it)
                adapter.notifyDataSetChanged()
                if(emptytvhistory.visibility!= View.GONE)
                    emptytvhistory.visibility= View.GONE
            }
            else
            {
                todolist.clear()
                adapter.notifyDataSetChanged()
                if(adapter.itemCount==0)
                    emptytvhistory.visibility=View.VISIBLE
            }
        })
    }

    private fun swipeFun()
    {
        val todotouchcallback= object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean=false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction== ItemTouchHelper.RIGHT)
                {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().deleteTask(adapter.todolist[viewHolder.absoluteAdapterPosition])
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
                if(actionState== ItemTouchHelper.ACTION_STATE_SWIPE)
                {
                    val paint= Paint()
                    val icon: Bitmap
                    if(dX>0)
                    {

                        icon=BitmapFactory.decodeResource(resources,R.drawable.ic_del_icon_png)
                        paint.color= Color.parseColor("#FF0000")
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
        val todotouchhelper= ItemTouchHelper(todotouchcallback)
        todotouchhelper.attachToRecyclerView(rvhistory)
    }
}