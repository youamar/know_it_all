package com.example.knowitall.screens.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knowitall.R

class QuizAdapter(private val questions: List<String>) :
    RecyclerView.Adapter<QuizAdapter.ViewHolder>() {

    private val selectedAnswers = mutableMapOf<Int, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        val answers = question.substringAfter("\n").split("\n")
        holder.bind(question, answers)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    fun getSelectedAnswers(): Map<Int, String> {
        return selectedAnswers
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.textViewQuestion)

        fun bind(question: String, answers: List<String>) {
            questionTextView.text = question
        }
    }
}
