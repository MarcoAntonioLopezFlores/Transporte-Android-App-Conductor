package com.app.expresstaxiconductor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.models.Message
import com.app.expresstaxiconductor.preferences.PrefsApplication

class MessageAdapter(context:Context, var messages: ArrayList<Message>): BaseAdapter() {
    var context = context

    fun add(message:Message){
        messages.add(message)
    }

    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(p0: Int): Any {
        return  messages[p0]
    }

    override fun getItemId(p0: Int): Long {
        return  0
    }

    override fun getView(position: Int, view: View?,viewGroup:ViewGroup?): View {
        var holder=MessageViewHolder()
        var myView = view

        var messageInflater = LayoutInflater.from(context)
        var message = messages[position].descripcion
        if(messages[position].usuario.id == PrefsApplication.prefs.getData("user_id").toLong()){
            myView = messageInflater.inflate(R.layout.sent_message_item, null)
            holder.textMessage = myView.findViewById(R.id.txtMessageSent)

            holder.textMessage!!.text = message
        }else{
            myView = messageInflater.inflate(R.layout.received_message_item, null)
            holder.textMessage = myView.findViewById(R.id.txtMessageReceived)

            holder.textMessage!!.text = message
        }

        return  myView
    }
}

internal class MessageViewHolder{
    var textMessage:TextView?=null
}