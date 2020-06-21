package com.example.android.codelabs.navigation.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_table")
data class Contact (
    @PrimaryKey(autoGenerate = true)
    var contactId: Long,

    @ColumnInfo(name = "contact_email")
    var contactEmail: String?

)