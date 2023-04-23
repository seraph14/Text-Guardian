package com.example.safesend.ui.blocked

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.safesend.Utility.SMS
import com.example.safesend.db.DaoSMS
import com.example.safesend.db.SmsDatabase
import com.example.safesend.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BlockedViewModel(application: Application) : AndroidViewModel(application){
    val scope = CoroutineScope(SupervisorJob())
    private var messageDao: DaoSMS
    private var repo: MessageRepository
    val allMessages: LiveData<MutableList<SMS>>

    init {
        messageDao = SmsDatabase.getDatabase(application, scope).smsDao()
        repo = MessageRepository(application.applicationContext)
        allMessages = repo.allMessages
    }

    fun listenForMessage(): LiveData<MutableList<SMS>> {
        return allMessages
    }
}