package com.example.safesend.Utility
import androidx.room.*
import com.example.safesend.db.DaoSMS

@Entity(tableName = "sms_blocked")
data class SMS(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name="msgSender") val msgSender: String = "",
        @ColumnInfo(name="msgContent") val msgContent: String = "",
        @ColumnInfo(name="msgDate") val msgDate: Long = 0
)



