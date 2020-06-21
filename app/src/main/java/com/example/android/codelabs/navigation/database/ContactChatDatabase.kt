package com.example.android.codelabs.navigation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ContactChat::class], version = 1, exportSchema = false)
abstract class ContactChatDatabase : RoomDatabase() {

    abstract fun contactChatDao(): ContactChatDao

    companion object{
        @Volatile
        private var INSTANCE: ContactChatDatabase? = null

        fun getDatabase(
                context: Context
        ): ContactChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ContactChatDatabase::class.java,
                        "contact_chat_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }

}