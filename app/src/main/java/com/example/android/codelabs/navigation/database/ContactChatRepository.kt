package com.example.android.codelabs.navigation.database

import androidx.lifecycle.LiveData

class ContactChatRepository (private val contactChatDao: ContactChatDao, contactEmail: String?) {
    val getAllContactChat: LiveData<List<ContactChat>> = contactChatDao.getAllChat(contactEmail.toString())

    suspend fun insert(contactChat: ContactChat) {
        contactChatDao.insert(contactChat)
    }

    suspend fun delete(contactChat: ContactChat) {
        contactChatDao.delete(contactChat)
    }

    suspend fun deleteAll(){
        contactChatDao.deleteAll()
    }

    suspend fun update(contactChat: ContactChat){
        contactChatDao.update(contactChat)
    }

}