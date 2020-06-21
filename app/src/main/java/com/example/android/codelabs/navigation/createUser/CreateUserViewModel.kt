package com.example.android.codelabs.navigation.createUser


import android.app.Application
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.codelabs.navigation.NetworkUtils
import com.example.android.codelabs.navigation.database.User
import com.example.android.codelabs.navigation.database.UserDatabase
import com.example.android.codelabs.navigation.database.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


class CreateUserViewModel (application: Application) : AndroidViewModel(application) {

    var returnUserCreated = MutableLiveData(false)

    var userPassword : String? = null

    var userEmail : String? = null

    private val repository: UserRepository

    val thisApplication : Application

    lateinit var user: User

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)

        thisApplication = application
    }

    interface Endpoint {

        @POST("register")
        @Headers("Content-type: application/json")
        fun insertUser(@Body user: User) : Call<User>

    }

    fun insert() = viewModelScope.launch(IO) {

        user = User(0, "", "", false, false)

        user.userEmail = userEmail
        user.userPassword = userPassword

        insertApi(user)

    }

    fun deleteAll() = viewModelScope.launch(IO) {
        repository.deleteAll()
    }


    private suspend fun insertApi(user: User){

        withContext(IO) {
            val retrofitClient = NetworkUtils
                    .getRetrofitInstance("http://10.0.2.2:3000/user/")

            val endpoint = retrofitClient.create(Endpoint::class.java)
            val callback = endpoint.insertUser(user)

            callback.enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    viewModelScope.launch(Main) {
                        Toast.makeText(thisApplication, t.message, Toast.LENGTH_SHORT).show()
                        println("DEBUG:  Error = ${t.message.toString()}")
                    }
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    response.body()?.let {
                        if (user.userEmail == it.userEmail) {
                            viewModelScope.launch(IO) {
                                repository.insert(user)
                                withContext(Main) {
                                    val toast = Toast.makeText(thisApplication, "Usuário criado com sucesso!", Toast.LENGTH_LONG)
                                    toast.setGravity(Gravity.CENTER, 0, 0)
                                    toast.show()
                                    userEmail = ""
                                    userPassword = ""
                                    delay(1500)
                                    returnUserCreated.value = true
                                }
                            }
                        } else {
                            viewModelScope.launch(IO) {
                                withContext(Main) {
                                    Toast.makeText(thisApplication, "Erro ao criar usuário!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            })
        }
    }
}