package com.example.safesend

import android.app.ActionBar
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safesend.Utility.SMS
import com.example.safesend.adapters.DetailMessageAdapter
import com.example.safesend.adapters.MessagesAdapter

class MessageActivity : AppCompatActivity() {
    private var recycler: RecyclerView? = null
    private var recycleAdapter: DetailMessageAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        val actionBar: ActionBar? = actionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(findViewById(R.id.toolbar))


        val ir = intent.extras?.get("Sender")
        title = ir.toString()
        recycleAdapter = DetailMessageAdapter()
        recycler = findViewById(R.id.detail_rc_)
        recycler?.adapter = recycleAdapter
        recycler?.layoutManager = LinearLayoutManager(applicationContext)
        getIndividualSms(ir.toString())
    }

    private fun getIndividualSms(sender: String){
        val col_projection = arrayOf("_id", "address", "body","date")
        val selectionClause = "address IN (?)"
        val sa = arrayOf(sender)
        val cursor: Cursor? = applicationContext.contentResolver.query(Uri.parse("content://sms/inbox"), col_projection, selectionClause, sa, "date")
        val cursor_sender: Cursor? = applicationContext.contentResolver.query(Uri.parse("content://sms/sent"), col_projection, selectionClause, sa, "date")
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
        cursor?.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        val intent: Intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        return true
    }
}