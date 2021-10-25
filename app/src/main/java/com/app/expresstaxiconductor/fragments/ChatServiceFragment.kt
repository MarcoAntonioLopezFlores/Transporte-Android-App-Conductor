package com.app.expresstaxiconductor.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.adapters.MessageAdapter
import com.app.expresstaxiconductor.models.Message
import kotlinx.android.synthetic.main.fragment_chat_service.*



class ChatServiceFragment : AppCompatActivity() {
    private val messageList = ArrayList<Message>()
    private  lateinit var messageAdapter:MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chat_service)
        setSupportActionBar(findViewById(R.id.toolbarBackDetails))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chat"


        recyclerViewChat.layoutManager= LinearLayoutManager(this)
        messageList.add(Message("Hola","A12S3X",1))
        messageList.add(Message("Adios","xaxaxa",2))
        messageAdapter = MessageAdapter(this,messageList)

        recyclerViewChat.adapter=messageAdapter

        btnSendMessage.setOnClickListener{
            sendMessage(edtMessage.text.toString(),"125asv")
        }
    }

    private fun sendMessage(message:String, to:String){
        messageList.add(Message(message,to,1))
        edtMessage.setText("")
        messageAdapter.notifyDataSetChanged()

    }

    /*override fun onCreateView(
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

    }*/


}