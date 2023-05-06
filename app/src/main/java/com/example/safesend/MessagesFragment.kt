package com.example.safesend

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safesend.Utility.SMS
import com.example.safesend.adapters.DetailMessageAdapter

class MessagesFragment : Fragment() {
    private var recycler: RecyclerView? = null
    private var recycleAdapter: DetailMessageAdapter? = null
    private lateinit var sender: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        recycleAdapter = DetailMessageAdapter()
        recycler = view.findViewById(R.id.detail_rc_)
        recycler?.adapter = recycleAdapter
        recycler?.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        sender = MessagesFragmentArgs.fromBundle(requireArguments()).sender

        getIndividualSms()

        return view
    }

    private fun getIndividualSms(){
        val messages: ArrayList<SMS> = arrayListOf()
        getMessagesFromDatabase(messages, true)
        getMessagesFromDatabase(messages, false)

        messages.sortBy {
            it.msgDate
        }
        recycleAdapter?.setData(messages)
    }

    private fun getMessagesFromDatabase(messages: ArrayList<SMS>, isSent: Boolean) {
        val colProjection = arrayOf("_id", "address", "body","date")
        val selectionClause = "address IN (?)"
        val sa = arrayOf(sender)
        val cursor: Cursor? = requireActivity()
            .applicationContext.contentResolver
            .query(
                Uri.parse("content://sms/${if (isSent) "sent" else "inbox"}"),
                colProjection,
                selectionClause,
                sa,
                "date"
            )

        if (cursor?.moveToFirst() == true) { // must check the result to prevent exception
            do {
                val address: String = cursor.getString(1)
                val body: String = cursor.getString(2)
                val date: Long = cursor.getLong(3)
                val sms = SMS(0, if (isSent) "you" else address, body, date)
                messages.add(sms)
            } while (cursor.moveToNext())
        }
        cursor?.close()
    }

}