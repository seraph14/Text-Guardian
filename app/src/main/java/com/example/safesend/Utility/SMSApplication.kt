package com.example.safesend.Utility

import android.app.Application

class SMSApplication : Application() {


    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
//    val database by lazy { SmsDatabase.getDatabase(this,applicationScope) }
//    companion object{
//        val applicationScope = CoroutineScope(SupervisorJob())
//        val repository by lazy { MessageRepository(database.smsDao()) }
//    }

}