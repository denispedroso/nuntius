package com.example.android.codelabs.navigation.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hamcrest.EasyMock2Matchers.equalTo
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

class ContactRepositoryTest {

    private lateinit var repository: ContactRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val contactDao = ContactDatabase.getDatabase(context).contactDao()

        repository = ContactRepository(contactDao)

    }

    @Test
    @Throws(Exception::class)
    fun writeContactAndReadInList() {
        CoroutineScope(Dispatchers.IO).launch {
            val contact = Contact(0, "teste1000@gmail.com")
            repository.insert(contact)
            val byName = repository.getContact("teste@gmail.com")
            assertEquals(byName, contact)
        }
    }

}