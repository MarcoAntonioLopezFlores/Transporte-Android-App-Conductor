package com.app.expresstaxiconductor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.models.Message

class MessageAdapter(val context:Context, private val messageList: List<Message>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVED=1
    private val ITEM_SENT=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType==1){
            val view:View = LayoutInflater.from(context).inflate(R.layout.received_message_item,parent, false)
            ReceiveViewHolder(view)
        }else{
            val view:View = LayoutInflater.from(context).inflate(R.layout.sent_message_item,parent, false)
            SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val currentMessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java){
            holder as SentViewHolder
            holder.sentMessage.text = currentMessage.content
        }else{
            holder as ReceiveViewHolder
            holder.receivedMessage.text = currentMessage.content
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        return if(1==currentMessage.senderId){
            ITEM_SENT
        }else{
            ITEM_RECEIVED
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById(R.id.txtMessageSent)
    }

    class ReceiveViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val receivedMessage: TextView = itemView.findViewById(R.id.txtMessageReceived)
    }
}