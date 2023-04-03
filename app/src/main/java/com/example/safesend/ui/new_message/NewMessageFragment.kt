package com.example.safesend.ui.new_message

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.safesend.R
import android.provider.ContactsContract
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi

class NewMessageFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var newMessageViewModel: NewMessageViewModel
    val countryCode: String = "+251"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_new_message, container, false)

        var sms = SmsManager.getDefault()
        val sendBtn : ImageButton = root.findViewById(R.id.sendBtn)
        val receiverPhone: EditText = root.findViewById(R.id.recieverPhoneNo)
        val messageContent: EditText = root.findViewById(R.id.message_content)


        sendBtn.setOnClickListener {

            val localPendingIntent1 : PendingIntent = PendingIntent.getBroadcast(context, 0, Intent("SMS_SENT"), 0)
            context?.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (resultCode == Activity.RESULT_OK) {
                        Log.i("Success", "Message sent succefully")
                        receiverPhone.text.clear()
                        messageContent.text.clear()
                    } else {
                        Log.i("Success", "Message not sent succefully")
                        receiverPhone.text.clear()
                        messageContent.text.clear()
                    }
                }
            }, IntentFilter("SMS_SENT"))
            sms.sendTextMessage(extractPhoneNo(receiverPhone.text.toString().trim()),
                    null, messageContent.text.toString().trim(), localPendingIntent1, null)

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        listView = view.findViewById(R.id.list_view)
//        listView.adapter = adapter
    }

    fun extractPhoneNo(number: String) : String{
        var result: String = ""
        if (number.length == 10 && number.get(0) == '0') {
            result = countryCode + number.substring(1, number.length)
        } else if (number.length == 13 && number.startsWith(countryCode)){
            result = number
        }
        return result
    }

}
