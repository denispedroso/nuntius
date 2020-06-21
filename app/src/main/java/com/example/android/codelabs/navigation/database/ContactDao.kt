package com.example.android.codelabs.navigation.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao

interface ContactDao {

    @Query("SELECT * FROM contact_table")
    fun getAllContacts(): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Query("SELECT * from contact_table WHERE contact_email = :email")
    suspend fun getContact(email: String): Contact

    @Delete
    suspend fun delete(contact: Contact)

    @Query("DELETE FROM contact_table")
    suspend fun deleteAll()

}