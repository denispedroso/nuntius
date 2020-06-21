package com.example.android.codelabs.navigation.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ContactRepository (private val contactDao: ContactDao) {

    val allContacts: LiveData<List<Contact>> = contactDao.getAllContacts()

    suspend fun insert(contact: Contact) {
        contactDao.insert(contact)
    }

    suspend fun delete(contact: Contact) {
        contactDao.delete(contact)
    }

    suspend fun deleteAll(){
        contactDao.deleteAll()
    }

    suspend fun update(contact: Contact){
        contactDao.update(contact)
    }

    suspend fun getContact(email: String){
        contactDao.getContact(email)
    }
}