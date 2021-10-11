package com.example.todo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_new_task.*
import kotlinx.android.synthetic.main.todoitem.view.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(val todolist:List<ToDo>) : RecyclerView.Adapter<TodoAdapter.TodoItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.todoitem,parent,false)
        return TodoItemHolder(itemView)
    }

    override fun getItemId(position: Int): Long {
        return todolist[position].id
    }
    override fun getItemCount(): Int = todolist.size

    override fun onBindViewHolder(holder: TodoItemHolder, position: Int) {
        return holder.bind(todolist[position])
    }

    class TodoItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(toDo: ToDo) {
            with(itemView){
                todoTitle.text= toDo.title
                todoSubtitle.text=toDo.detail
                todoType.text=toDo.type
                updateTime(toDo.time)
                updateDate(toDo.date)
                var color = Color.argb(255,
                    Random().nextInt(256),
                    Random().nextInt(256),
                    Random().nextInt(256))
                itemView.backColorView.setBackgroundColor(color)
            }
        }
        private fun updateDate(date:Long) {

            val date_string="EEE, d MMM yyyy"
            var sdf= SimpleDateFormat(date_string)
            itemView.datetv.text=sdf.format(Date(date))
        }

        private fun updateTime(time:Long) {

            val time_string="h:mm a"
            var sdf= SimpleDateFormat(time_string)
            itemView.timetv.text=sdf.format(Date(time))
        }
    }

}