package com.example.android.codelabs.navigation.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Defines methods for using the User class with Room.
 */

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user_table ORDER BY userId DESC LIMIT 1")
    fun get(): LiveData<User>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * from user_table WHERE user_email = :email")
    suspend fun getUser(email: String): User

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()


}