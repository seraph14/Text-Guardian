package com.example.safesend.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.safesend.Utility.SMS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [SMS::class], version = 1)
abstract class SmsDatabase : RoomDatabase() {
    abstract fun smsDao(): DaoSMS

    companion object{
        // create singleton class
        @Volatile
        private var INSTANCE: SmsDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SmsDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        SmsDatabase::class.java,
                        "sms_database"
                ).addCallback(SmsDatabaseCallback(scope)).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class SmsDatabaseCallback(
            private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
//                    populateDatabase(database.smsDao())
                }
            }
        }

        suspend fun populateDatabase(smsDao: DaoSMS) {
            val s = SMS(0, "+251942230327","Ere dude negn accept arga...")
            smsDao.insertAll(s)
        }
    }

}