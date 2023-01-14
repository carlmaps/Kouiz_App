package com.example.kouizapp.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Question::class], version = 3)
abstract class KouizDB : RoomDatabase(){
    abstract fun questionsDao(): QuestionDao

    companion object {
        //  means that this field is immediately made visible to other threads
        @Volatile private var instance : KouizDB? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            // Create a instance also return the instance
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        // Function to build database
        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            KouizDB::class.java,
            "kouizDB"
        ).createFromAsset("kouizDB.db").build()
    }
}