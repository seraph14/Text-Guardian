package com.example.safesend.repository

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.safesend.Utility.SMS
import com.example.safesend.db.DaoSMS
import com.example.safesend.db.SmsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.sql.Date
import java.text.SimpleDateFormat


object MessageRepository {
    private lateinit var messageDao: DaoSMS
    private val scope = CoroutineScope(SupervisorJob())
    lateinit var allMessages: LiveData<MutableList<SMS>>
    operator fun invoke(context: Context): MessageRepository {
        messageDao = SmsDatabase.getDatabase(context, scope).smsDao();
        allMessages = messageDao.getAllSms().asLiveData()
        return this
    }

    fun insertToList(sms: SMS){
        allMessages.value?.add(sms)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(sms: SMS) {
        messageDao.insertAll(sms)
    }

    fun readSms(context: Context): List<SMS>{
        val col_projection = arrayOf("_id", "address", "body","date")
        val cursor_inbox: Cursor? = context.contentResolver.query(Uri.parse("content://sms/inbox"), col_projection, null, null, "date DESC")
        val cursor_outbox: Cursor? = context.contentResolver.query(Uri.parse("content://sms/sent"), col_projection, null, null, "date DESC")
        var inboxSms = ArrayList<SMS>()
        if (cursor_inbox?.moveToFirst() == true) { // must check the result to prevent exception
            do {
                val id: String = cursor_inbox.getString(0)
                val address: String = cursor_inbox.getString(1)
                val body: String = cursor_inbox.getString(2)
                val date: Long = cursor_inbox.getLong(3)
                val smsInbox = SMS(0, address, body, date)
                inboxSms.add(smsInbox)
            } while (cursor_inbox.moveToNext())
        }
        if (cursor_outbox?.moveToFirst() == true) { // must check the result to prevent exception
            do {
                val id: String = cursor_outbox.getString(0)
                val address: String = cursor_outbox.getString(1)
                val body: String = cursor_outbox.getString(2)
                val date: Long = cursor_outbox.getLong(3)
                val smsInbox = SMS(0, address, body, date)
                inboxSms.add(smsInbox)
            } while (cursor_outbox.moveToNext())
        }
        cursor_inbox?.close()
        var filteredSms = inboxSms.sortedByDescending {
            it.msgDate
        }
        filteredSms = filteredSms.distinctBy { it.msgSender }
        return filteredSms
    }

    suspend fun storeSms(context: Context, sms: SMS){
        val content = ContentValues()
        content.put("address", sms.msgSender);
        content.put("body", sms.msgContent);
        content.put("date", sms.msgDate);
        context.contentResolver.insert(Uri.parse("content://sms/inbox"), content)
    }


}

