package com.example.safesend.adapters

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.safesend.MessageActivity
import com.example.safesend.R
import com.example.safesend.Utility.SMS
import com.example.safesend.models.MessageModel
import com.google.android.material.internal.ContextUtils.getActivity
import java.lang.Error
import java.security.AccessController.getContext
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(ctx: Context?): RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>(), Filterable {
    var msgs = mutableListOf<SMS>()
    var msgsFull = mutableListOf<SMS>()
    var context = ctx
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_card, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val m = msgs.get(position)
        holder.bind(m)
        holder.itemView.setOnClickListener {
            val i = Intent(context, MessageActivity::class.java)
            i.putExtra("Sender", m.msgSender)
            i.flags = FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(i)
        }
    }
    override fun getItemCount(): Int {
        return msgs.size
    }

    override fun getFilter(): Filter {
        return  msgsFilter
    }

//    Search filter is implemented here
    private  val msgsFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<SMS>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(msgsFull)
            } else {
                val filterPattern: String = constraint.toString().toLowerCase(Locale.ROOT).trim()
                for (item in msgs) {
                    if (filterPattern in item.msgSender.toLowerCase(Locale.ROOT).trim() ||
                        filterPattern in item.msgContent.toLowerCase(Locale.ROOT).trim()) {
                        filteredList.add(item)
                    }
                }
            }
            val results: FilterResults = FilterResults()
            results.values = filteredList
            return  results

        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            msgs.clear()
            msgs.addAll(results?.values as List<SMS>)
            notifyDataSetChanged()
        }
    }

    fun setData(messages: MutableList<SMS>){
        this.msgs = messages
        this.msgsFull.addAll(this.msgs)
        notifyDataSetChanged()
    }
    class MessageViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val sender: TextView = itemView.findViewById(R.id.msg_sender)
        private val msg: TextView = itemView.findViewById(R.id.msg_content)
        private val date: TextView = itemView.findViewById(R.id.msg_date)
        fun bind(sms: SMS){
            val dateFormat = SimpleDateFormat("MM/dd/yyyy").format(Date(sms.msgDate))
            sender.text = sms.msgSender
            msg.text = sms.msgContent
            date.text = dateFormat
        }
    }
}