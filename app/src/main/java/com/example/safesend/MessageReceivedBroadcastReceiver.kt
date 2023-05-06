package com.example.safesend

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.safesend.Utility.SMS
import com.example.safesend.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class MessageReceivedBroadcastReceiver : BroadcastReceiver() {

    val CHANNEL_ID = "channelId"
    val CHANNEL_NAME = "channelName"

    override fun onReceive(context: Context, intent: Intent?) {
        val scope = CoroutineScope(SupervisorJob())
//        val dao = SmsDatabase.getDatabase(context, scope).smsDao()
        val repo = MessageRepository(context)
        val sms = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0]
        createNotificationChannel(context)
        val dateTimeSms: Long = sms.timestampMillis
        val sender = sms.originatingAddress.toString()
        val newSms = SMS(0, sender, sms.displayMessageBody, msgDate = dateTimeSms)
        if (isScam(sender)) {
//               if scam store it in blocked list and abort msg broadcast
               Toast.makeText(context, "Scam SMS blocked", Toast.LENGTH_LONG).show()
               abortBroadcast()
               scope.launch {
                   repo.insert(newSms)
               }

           }else {
                // If not scam then display notification and add msg to
                showNotification(context, sms)
                scope.launch {
                    repo.storeSms(context, newSms)
                    repo.insertToList(newSms)
                }
           }
    }

    private fun isScam(num: String): Boolean{
        val p = Pattern.compile("[0-9]{4}")
        if(p.matcher(num).matches()){
            return true
        }
        return false
    }

    private fun showNotification(context: Context?, sms: SmsMessage) {
        val notification = context?.let {
            NotificationCompat.Builder(it, CHANNEL_ID)
                .setContentTitle(sms.originatingAddress)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.profile_pic))
                .setContentText(sms.displayMessageBody)
                .setSmallIcon(R.drawable.ic_info_about)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        }


        val notificationManager = context?.let { NotificationManagerCompat.from(it) }
        if (notification != null) {
            notificationManager?.notify(0, notification)
        }
    }

    private fun createNotificationChannel(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chanel)
        }
    }

}