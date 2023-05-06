package com.example.safesend.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.safesend.Utility.SMS
import com.example.safesend.db.DaoSMS
import com.example.safesend.db.SmsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


object MessageRepository {
    private lateinit var messageDao: DaoSMS
    private val scope = CoroutineScope(SupervisorJob())
    lateinit var allMessages: LiveData<MutableList<SMS>>
    operator fun invoke(context: Context): MessageRepository {
        messageDao = SmsDatabase.getDatabase(context, scope).smsDao()
        allMessages = messageDao.getAllSms().asLiveData()
        return this
    }

    fun insertToList(sms: SMS){
        allMessages.value?.add(sms)
    }

    @WorkerThread
    suspend fun insert(sms: SMS) {
        messageDao.insertAll(sms)
    }

    fun readSms(context: Context): List<SMS>{
        val messages = ArrayList<SMS>()
        getSMSFromDB(messages, true, context)
        getSMSFromDB(messages, false, context)
        var filteredSms = messages.distinctBy { it.msgSender }
        filteredSms = filteredSms.sortedByDescending {
            it.msgDate
        }
        return filteredSms
    }

    private fun getSMSFromDB(messages: ArrayList<SMS>, isSent: Boolean, context: Context) {
        val colProjection = arrayOf("_id", "address", "body","date")
        val cursor: Cursor? = context.contentResolver.query(
            Uri.parse("content://sms/${if (isSent) "sent" else "inbox"}"),
            colProjection,
            null,
            null,
            "date DESC"
        )

        if (cursor?.moveToFirst() == true) { // must check the result to prevent exception
            do {
                val id: String = cursor.getString(0)
                val address: String = cursor.getString(1)
                val body: String = cursor.getString(2)
                val date: Long = cursor.getLong(3)
                val sms = SMS(0, address, body, date)
                messages.add(sms)
            } while (cursor.moveToNext())
        }
        cursor?.close()
    }

    suspend fun storeSms(context: Context, sms: SMS){
        val content = ContentValues()
        content.put("address", sms.msgSender)
        content.put("body", sms.msgContent)
        content.put("date", sms.msgDate)
        context.contentResolver.insert(Uri.parse("content://sms/inbox"), content)
    }


}

