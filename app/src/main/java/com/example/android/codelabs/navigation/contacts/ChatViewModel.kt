package com.example.android.codelabs.navigation.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.codelabs.navigation.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel (application: Application, private val contactEmail: String) : AndroidViewModel(application) {

    private val repository: ContactChatRepository

    private val thisApplication : Application

    val messageToSend = MutableLiveData("")

    val allChat : LiveData<List<ContactChat>>

    init {
        val contactChatDao = ContactChatDatabase.getDatabase(application).contactChatDao()
        repository = ContactChatRepository(contactChatDao, contactEmail)
        thisApplication = application
        allChat = repository.getAllContactChat
    }

    fun insert(chat: ContactChat) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(chat)
    }
}