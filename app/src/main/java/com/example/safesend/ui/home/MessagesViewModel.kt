package com.example.safesend.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.safesend.Utility.SMS
import com.example.safesend.db.DaoSMS
import com.example.safesend.db.SmsDatabase
import com.example.safesend.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class MessagesViewModel(private val app: Application) : AndroidViewModel(app) {
    val scope = CoroutineScope(SupervisorJob())
    private val messageDao: DaoSMS = SmsDatabase.getDatabase(app, scope).smsDao()
    private val repo: MessageRepository = MessageRepository(app.applicationContext)
    val allInbox: MutableLiveData<MutableList<SMS>> = MutableLiveData()
    init {
        allInbox.postValue(getMs())
    }
    private fun getMs(): MutableList<SMS>{
       return repo.readSms(app.applicationContext).toMutableList()
    }
}