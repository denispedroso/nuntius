package com.example.android.codelabs.navigation.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao

interface ContactChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contactChat: ContactChat)

    @Query("SELECT * FROM contact_chat WHERE emailRecipient = :email OR emailSender = :email")
    fun getAllChat(email: String): LiveData<List<ContactChat>>

    @Update
    suspend fun update(contactChat: ContactChat)

    @Delete
    suspend fun delete(contactChat: ContactChat)

    @Query("DELETE FROM contact_chat")
    suspend fun deleteAll()

}