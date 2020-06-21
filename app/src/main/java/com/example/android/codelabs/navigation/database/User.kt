package com.example.android.codelabs.navigation.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *       Represents a User with its email and password
 */
@Entity(tableName = "user_table")
class User(
        @PrimaryKey(autoGenerate = true)
        var userId: Long,

        @ColumnInfo(name = "user_email")
        var userEmail: String?,

        @ColumnInfo(name = "user_password")
        var userPassword: String?,

        @ColumnInfo(name = "user_checked")
        var userChecked: Boolean,

        @ColumnInfo(name = "user_logged")
        var userLogged: Boolean
)
