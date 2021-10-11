package com.example.todo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ToDoDAO {

    @Insert
    suspend fun insert(task:ToDo):Long
    @Query("Select * From ToDo Where done==0")
    fun getTasksList():LiveData<List<ToDo>>
    @Query("Select * From ToDo Where done==1")
    fun getDoneTasks():LiveData<List<ToDo>>
    @Query("Update ToDo Set  done=1 Where id=:taskID")
    fun taskDone(taskID:Long);
    @Delete
    suspend fun deleteTask(task:ToDo)
    @Query("Select * From ToDo Where id=:taskID")
    fun getByID(taskID:Long):ToDo

}