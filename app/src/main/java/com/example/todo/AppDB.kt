package com.example.todo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ToDo::class],version = 1)
abstract class AppDB: RoomDatabase() {
    abstract fun todoDao():ToDoDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDB?=null

        fun getDatabase(context: Context):AppDB
        {
            val tempInstance= INSTANCE
            if(tempInstance!=null)
                return tempInstance
            synchronized(this)
            {
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    DB_NAME
                ).build()
                INSTANCE=instance
                return instance
            }
        }
    }
}