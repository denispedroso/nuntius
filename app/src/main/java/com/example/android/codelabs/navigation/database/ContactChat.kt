package com.example.android.codelabs.navigation.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime
import java.util.*

@Entity(tableName = "contact_chat")
class ContactChat(
        @PrimaryKey(autoGenerate = true)
        var id: Long,
        @ColumnInfo
        var emailSender : String,
        @ColumnInfo
        var emailRecipient : String,
        @ColumnInfo
        var self : Boolean,
        @ColumnInfo
        var createdAt: Int,
        @ColumnInfo
        var message: String
)