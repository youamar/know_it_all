package com.example.knowitall.screens.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowitall.MainActivity
import com.example.knowitall.R

class QuizFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        recyclerView = view.findViewById(R.id.quizRecyclerView)
        submitButton = view.findViewById(R.id.submitButton)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        super.onViewCreated(view, savedInstanceState)

        val questions = arguments?.getStringArrayList("questions")
        if (questions != null) {
            val adapter = QuizAdapter(questions)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            submitButton.setOnClickListener {
                val answers = adapter.getSelectedAnswers()
                findNavController().navigate(R.id.action_quizFragment_to_sentFragment)
            }
        }
    }
}