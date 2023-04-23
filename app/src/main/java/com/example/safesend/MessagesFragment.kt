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
        val sender = MessagesFragmentArgs.fromBundle(requireArguments()).sender

        getIndividualSms(sender)

        return view
    }

    private fun getIndividualSms(sender: String){
        val col_projection = arrayOf("_id", "address", "body","date")
        val selectionClause = "address IN (?)"
        val sa = arrayOf(sender)
        val cursor: Cursor? = requireActivity().applicationContext.contentResolver.query(Uri.parse("content://sms/inbox"), col_projection, selectionClause, sa, "date")
        val cursor_sender: Cursor? = requireActivity().applicationContext.contentResolver.query(Uri.parse("content://sms/sent"), col_projection, selectionClause, sa, "date")
        val singleUser = ArrayList<SMS>()
        if (cursor?.moveToFirst() == true) { // must check the result to prevent exception
            do {
                val address: String = cursor.getString(1)
                val body: String = cursor.getString(2)
                val date: Long = cursor.getLong(3)
                val smsInbox = SMS(0, address, body, date)
                singleUser.add(smsInbox)
                Log.i("Messages: ", "$address $body")
            } while (cursor.moveToNext())
        }
        cursor?.close()
        if (cursor_sender?.moveToFirst() == true) { // must check the result to prevent exception
            do {
                val address: String = cursor_sender.getString(1)
                val body: String = cursor_sender.getString(2)
                val date: Long = cursor_sender.getLong(3)
                val smsInbox = SMS(0, "You", body,date)
                singleUser.add(smsInbox)
                Log.i("Messages: ", "$address $body")
            } while (cursor_sender.moveToNext())
        }
        singleUser.sortBy {
            it.msgDate
        }
        recycleAdapter?.setData(singleUser)
    }
}