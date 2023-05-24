package com.example.knowitall.screens.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knowitall.R

class QuizAdapter(
    private val questions: List<String>,
    private val answerSelectionListener: AnswerSelectionListener,
    private val questionIndex: Int
) : RecyclerView.Adapter<QuizAdapter.ViewHolder>() {

    private val selectedAnswers = mutableMapOf<Int, String>()
    private val nbQuestions = 5
    private val nbAnswers = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question, questionIndex + position)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    fun getContent(): String {
        val contentBuilder = StringBuilder()
        for (question in questions) {
            contentBuilder.append(question).append("\n")
        }
        return contentBuilder.toString()
    }

    interface AnswerSelectionListener {
        fun onAnswerSelected(questionIndex: Int, selectedAnswer: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(question: String, position: Int) {
            val quiz = question.substringAfter("\n").split("\n")
            val questionList = mutableListOf<String>()
            val answersList = mutableListOf<List<String>>()

            for (i in 1 until quiz.size step nbQuestions) {
                val q = quiz[i]
                questionList.add(q)
            }

            for (i in 2 until quiz.size step nbQuestions) {
                val answers = mutableListOf<String>()
                for (j in i until i + nbAnswers) {
                    val answer = quiz[j]
                    answers.add(answer)
                }
                answersList.add(answers)
            }

            val layoutInflater = LayoutInflater.from(itemView.context)
            val parentView = itemView as ViewGroup

            parentView.removeAllViews() // Clear existing views

            for (index in questionList.indices) {
                val questionView = layoutInflater.inflate(
                    R.layout.item_question,
                    parentView,
                    false
                )
                val questionTextView: TextView =
                    questionView.findViewById(R.id.questionTextView)
                val radioGroupAnswers: RadioGroup =
                    questionView.findViewById(R.id.radioGroupAnswers)
                val radioButton1: RadioButton =
                    questionView.findViewById(R.id.radioButton1)
                val radioButton2: RadioButton =
                    questionView.findViewById(R.id.radioButton2)
                val radioButton3: RadioButton =
                    questionView.findViewById(R.id.radioButton3)

                questionTextView.text = questionList[index]

                radioButton1.text = answersList[index][0]
                radioButton2.text = answersList[index][1]
                radioButton3.text = answersList[index][2]

                radioGroupAnswers.setOnCheckedChangeListener { _, checkedId ->
                    val selectedAnswer = when (checkedId) {
                        R.id.radioButton1 -> radioButton1.text.toString()
                        R.id.radioButton2 -> radioButton2.text.toString()
                        R.id.radioButton3 -> radioButton3.text.toString()
                        else -> ""
                    }
                    selectedAnswers[position] = selectedAnswer
                    answerSelectionListener.onAnswerSelected(position, selectedAnswer)
                }
                parentView.addView(questionView)
            }
        }
    }
}