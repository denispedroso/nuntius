package com.example.android.codelabs.navigation.database

import androidx.lifecycle.LiveData

class UserRepository (private val userDao: UserDao) {

    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    val user: LiveData<User>? = userDao.get()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun delete(user: User){
        userDao.delete(user)
    }

    suspend fun deleteAll(){
        userDao.deleteAll()
    }

    suspend fun update(user: User){
        userDao.update(user)
    }

    suspend fun getUser(email: String): User {
        return userDao.getUser(email)
    }

    private fun getUser() {
        userDao.get()
    }
}