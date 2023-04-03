package com.example.safesend.ui.home

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.safesend.Utility.SMS
import com.example.safesend.db.DaoSMS
import com.example.safesend.db.SmsDatabase
import com.example.safesend.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class MessagesViewModel(private val app: Application) : AndroidViewModel(app) {
    val scope = CoroutineScope(SupervisorJob())
    private var messageDao: DaoSMS
    private var repo: MessageRepository
    val allInbox: MutableLiveData<MutableList<SMS>> = MutableLiveData()
    init {
        messageDao = SmsDatabase.getDatabase(app, scope).smsDao();
        repo = MessageRepository(app.applicationContext)
        allInbox.postValue(getMs())
    }
    private fun getMs(): MutableList<SMS>{
       return repo.readSms(app.applicationContext).toMutableList()
    }
}