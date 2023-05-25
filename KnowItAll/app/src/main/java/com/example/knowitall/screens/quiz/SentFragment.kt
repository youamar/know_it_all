package com.example.knowitall.screens.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.knowitall.MainActivity
import com.example.knowitall.R

class SentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).showLoginFragment()
        super.onViewCreated(view, savedInstanceState)
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        val okButton = view.findViewById<Button>(R.id.okButton)
        okButton.setOnClickListener {
            findNavController().navigate(R.id.action_sentFragment_to_loginFragment)
            (activity as MainActivity).hideLoginFragment()
        }
    }

}