package com.example.android.codelabs.navigation.contacts

import android.app.Application
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.codelabs.navigation.NetworkUtils
import com.example.android.codelabs.navigation.Utils.BodyResponse
import com.example.android.codelabs.navigation.database.*
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


class ContactsViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: ContactRepository

    private val repositoryChat: ContactChatRepository

    private val thisApplication : Application

    private lateinit var contact: Contact

    val allContacts : LiveData<List<Contact>>

    val emailToInsert = MutableLiveData("")

    var returnContactInserted = MutableLiveData(false)


    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(contactDao)

        val chatDao = ContactChatDatabase.getDatabase(application).contactChatDao()
        repositoryChat = ContactChatRepository(chatDao, null)

        thisApplication = application
        allContacts = repository.allContacts

    }

    interface Endpoint {
        @GET("find/{email}")
        fun getContact(@Path("email") email: String?): Call<BodyResponse>
    }

    fun insert() = viewModelScope.launch(Dispatchers.IO) {

        contact = Contact(0, "")

        contact.contactEmail = emailToInsert.value

        getContactApi(contact)
    }

    fun delete(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        println("DEBUG: Entrou delete()")
        repository.delete(contact)
    }

    fun insertMessage(contactChat: ContactChat) = viewModelScope.launch(Dispatchers.IO) {
        repositoryChat.insert(contactChat)
    }

    private suspend fun getContactApi(contact: Contact) {
        withContext(Dispatchers.IO) {
            val retrofitClient = NetworkUtils
                    .getRetrofitInstance("http://10.0.2.2:3000/user/")

            val endpoint = retrofitClient.create(ContactsViewModel.Endpoint::class.java)
            val callback = endpoint.getContact(emailToInsert.value)


            callback.enqueue(object : Callback<BodyResponse> {
                override fun onFailure(call: Call<BodyResponse>, t: Throwable) {
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(thisApplication, t.message, Toast.LENGTH_SHORT).show()
                        println("DEBUG:  Error = ${t.message.toString()}")
                    }
                }

                override fun onResponse(call: Call<BodyResponse>, response: Response<BodyResponse>) {
                    response.body()?.let {
                        if (emailToInsert.value == it.data?.email) {
                            viewModelScope.launch(Dispatchers.IO) {
                                println("DEBUG: entrou response OK")
                                repository.insert(contact)
                                withContext(Dispatchers.Main) {
                                    val toast = Toast.makeText(thisApplication, "Contato Inserido!", Toast.LENGTH_LONG)
                                    toast.setGravity(Gravity.CENTER, 0, 0)
                                    toast.show()
                                    delay(1500)
                                    returnContactInserted.value = true
                                }
                            }
                        } else {
                            viewModelScope.launch(Dispatchers.IO) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(thisApplication, "Contato inválido!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        println("DEBUG: retorno de login $it")
                    }
                }
            })
        }
    }
}