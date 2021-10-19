package com.app.expresstaxiconductor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.expresstaxiconductor.R
import kotlinx.android.synthetic.main.fragment_details_driver.view.*


class DetailsDriverFragment:Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_details_driver, container, false)

        viewRoot.btnChatService.setOnClickListener{
            findNavController().navigate(R.id.action_detailsFragment_to_chatServiceFragment)
        }

        viewRoot.btnStartService.setOnClickListener{
            viewRoot.btnStartService.visibility = View.GONE
            viewRoot.btnFinishService.visibility = View.VISIBLE
            viewRoot.btnCancelService.visibility = View.GONE
            viewRoot.containerBtnChatService.visibility = View.VISIBLE
        }

        viewRoot.btnCancelService.setOnClickListener{

            findNavController().navigate(R.id.action_detailsFragment_to_mapsFragment)

        }

        viewRoot.btnFinishService.setOnClickListener{
            findNavController().navigate(R.id.action_detailsFragment_to_mapsFragment)

        }

        return viewRoot
    }


}