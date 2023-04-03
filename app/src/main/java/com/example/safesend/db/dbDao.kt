package com.example.safesend.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.safesend.Utility.SMS
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoSMS{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMessage(sms: SMS)


    @Query("SELECT * FROM sms_blocked ORDER BY msgSender ASC")
    fun getAllSms(): Flow<MutableList<SMS>>
//
//    @Query("SELECT * FROM SMS WHERE id IN (:smsId)")
//    fun loadAllByIds(smsId: IntArray): List<SMS>
//
    @Insert
    suspend fun insertAll(vararg sms: SMS)
//
//    @Delete
//    fun delete(sms: SMS)
}