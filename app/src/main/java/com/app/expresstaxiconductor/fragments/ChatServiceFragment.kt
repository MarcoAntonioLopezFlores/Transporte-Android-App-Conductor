package com.app.expresstaxiconductor.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.adapters.MessageAdapter
import com.app.expresstaxiconductor.models.Message
import kotlinx.android.synthetic.main.fragment_chat_service.view.*


class ChatServiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewRoot =inflater.inflate(R.layout.fragment_chat_service, container, false)

        val messageList = ArrayList<Message>()
        viewRoot.recyclerViewChat.layoutManager= LinearLayoutManager(context)
        messageList.add(Message("Hola","A12S3X",1))
        messageList.add(Message("Adios","xaxaxa",2))
        var myAdapterMessage = activity?.let {
            MessageAdapter(it,messageList)
        }

        viewRoot.recyclerViewChat.adapter=myAdapterMessage

        myAdapterMessage!!.notifyDataSetChanged()
        viewRoot.btnSendMessage.setOnClickListener{
            Toast.makeText(context, "Mensaje", Toast.LENGTH_SHORT).show()
        }
        return viewRoot

    }


}

