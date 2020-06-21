package com.example.android.codelabs.navigation.home

import android.app.Application
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.codelabs.navigation.NetworkUtils
import com.example.android.codelabs.navigation.Utils.BodyResponse
import com.example.android.codelabs.navigation.createUser.CreateUserViewModel
import com.example.android.codelabs.navigation.database.User
import com.example.android.codelabs.navigation.database.UserDatabase
import com.example.android.codelabs.navigation.database.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    val allUsers : LiveData<List<User>>

    val user : LiveData<User>?

    val gottenUser = MutableLiveData(" ")

    val loggedUser = MutableLiveData<User>()

    var gottenPassword : String?  = null

    private lateinit var userToSend: BodyToSend

    val thisApplication : Application

    var returnUserLoggedIn = MutableLiveData(false)


    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        user = repository.user
        allUsers = repository.allUsers
        thisApplication = application
    }

    interface Endpoint {
        @POST("login")
        @Headers("Content-type: application/json")
        fun logInUser(@Body bodyToSend: BodyToSend) : Call<BodyResponse>
    }

    data class BodyToSend (
        var email: String?,
        var password: String?
    )

    data class NUser (
        val email: String?
    )

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(user: User) = viewModelScope.launch(IO) {
        repository.insert(user)
    }

    fun logIn() = viewModelScope.launch(IO) {

        userToSend = BodyToSend("", "")

        userToSend.email = gottenUser.value
        userToSend.password = gottenPassword

        loginApi(userToSend)
    }

    fun saveLoggedIn(userEmail: String) = viewModelScope.launch(IO) {

        var user: User = repository.getUser(userEmail)

        if(user == null) {
            user = User(0, "", "", false, false)
            user.userEmail = userToSend.email
            user.userPassword = userToSend.password
            repository.insert(user)
        }

        user.userLogged = true

        repository.update(user)

    }

    private suspend fun loginApi(bodyToSend: BodyToSend) {
        withContext(IO) {
            val retrofitClient = NetworkUtils
                    .getRetrofitInstance("http://10.0.2.2:3000/user/")

            val endpoint = retrofitClient.create(HomeViewModel.Endpoint::class.java)
            val callback = endpoint.logInUser(bodyToSend)

            callback.enqueue(object : Callback<BodyResponse> {
                override fun onFailure(call: Call<BodyResponse>, t: Throwable) {
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(thisApplication, t.message, Toast.LENGTH_SHORT).show()
                        println("DEBUG:  Error = ${t.message.toString()}")
                    }
                }

                override fun onResponse(call: Call<BodyResponse>, response: Response<BodyResponse>) {
                    response.body()?.let {
                        if (bodyToSend.email == it.data?.email) {
                            saveLoggedIn(bodyToSend.email.toString())
                            viewModelScope.launch(IO) {
                                println("DEBUG: entrou response OK")
                                withContext(Dispatchers.Main) {
                                    val toast = Toast.makeText(thisApplication, "Usuário loggado!", Toast.LENGTH_LONG)
                                    toast.setGravity(Gravity.CENTER, 0, 0)
                                    toast.show()
                                    delay(1000)
                                    loggedUser.value = repository.getUser(bodyToSend.email.toString())
                                    returnUserLoggedIn.value = true
                                }
                            }
                        } else {
                            if (it.error!!) {
                                Toast.makeText(thisApplication, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(thisApplication, "Usuário ou senha inválido!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        println("DEBUG: retorno de error  ${it.error.toString()}")
                        println("DEBUG: retorno de message ${it.message}")
                        println("DEBUG: retorno de data ${it.data}")
                    }
                }
            })
        }
    }

    fun deleteAll() = viewModelScope.launch(IO) {
        repository.deleteAll()
    }

}