package com.example.safesend.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safesend.R
import com.example.safesend.Utility.SMS

class BlockedMessagesAdapter: RecyclerView.Adapter<BlockedMessagesAdapter.BlockedMessageViewHolder>() {
    var msgs = emptyList<SMS>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedMessageViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_card, parent, false)
        return BlockedMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockedMessageViewHolder, position: Int) {
        val m = msgs.get(position)
        holder.bind(m)
    }
    override fun getItemCount(): Int {
        return msgs.size
    }
    fun setData(messages: List<SMS>){
        this.msgs = messages
        notifyDataSetChanged()
    }
    class BlockedMessageViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val sender: TextView = itemView.findViewById(R.id.msg_sender)
        private val msg: TextView = itemView.findViewById(R.id.msg_content)

        fun bind(sms: SMS){
            sender.text = sms.msgSender
            msg.text = sms.msgContent
        }
    }
}
