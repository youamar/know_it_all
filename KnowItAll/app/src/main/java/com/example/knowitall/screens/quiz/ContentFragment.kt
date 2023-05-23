package com.example.knowitall.screens.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.knowitall.MainActivity
import com.example.knowitall.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ContentFragment : Fragment() {
    private lateinit var contentText: TextView
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var questions: List<String>
    private var numQuestions = 5
    private var numAnswers = 3
    private lateinit var loadingView: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_content, container, false)
        contentText = view.findViewById(R.id.contentText)
        loadingView = view.findViewById(R.id.loadingView)
        okHttpClient = OkHttpClient()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title")
        val content = arguments?.getString("content")

        val combinedText = "$title\n\n$content"

        contentText.text = combinedText

        val startButton = view.findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            showLoadingView()
            generateQuestions(title, content, numQuestions, numAnswers) { generatedQuestions ->
                questions = generatedQuestions
                val bundle = Bundle().apply {
                    putStringArrayList("questions", ArrayList(questions))
                }
                view.post {
                    hideLoadingView()
                    if (generatedQuestions.isNotEmpty()) {
                        try {
                            findNavController().navigate(R.id.action_contentFragment_to_quizFragment, bundle)
                        } catch (exception: IllegalArgumentException) {
                            exception.printStackTrace()
                        }
                    } else {
                        try {
                            findNavController().navigate(R.id.action_contentFragment_to_errorFragment)
                        } catch (exception: IllegalArgumentException) {
                            exception.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun generateQuestions(
        title: String?,
        content: String?,
        numQuestions: Int,
        numAnswers: Int,
        callback: (List<String>) -> Unit
    ) {
        val url = "https://api.openai.com/v1/completions"
        val prompt = "Assume the format of each question should start with a numeric question " +
                "number, followed by the question text, and then options A, B, and C with their " +
                "respective answers, ex : 1.what is your name ? (new line) A.not (new line) B.your " +
                "(new line) C.father, it should follow the format of the example, " +
                "generate $numQuestions questions with $numAnswers possible answers each " +
                "based on the following content:\n$content"
        val maxTokens = 300
        val temperature = 0.8

        val jsonObject = JSONObject()
        jsonObject.put("model", "text-davinci-003")
        jsonObject.put("prompt", prompt)
        jsonObject.put("max_tokens", maxTokens)
        jsonObject.put("temperature", temperature)

        val requestBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val randomString = getString(R.string.random_string3)
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $randomString")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    hideLoadingView()
                    try {
                        findNavController().navigate(R.id.action_contentFragment_to_errorFragment)
                    } catch (exception: IllegalArgumentException) {
                        exception.printStackTrace()
                    }
                }
                callback.invoke(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                try {
                    val responseObject = JSONObject(responseBody)

                    val choicesArray = responseObject.getJSONArray("choices")
                    if (choicesArray.length() > 0) {
                        val generatedQuestions = mutableListOf<String>()
                        for (i in 0 until choicesArray.length()) {
                            val question = choicesArray.getJSONObject(i).getString("text")
                            generatedQuestions.add(question)
                        }
                        callback.invoke(generatedQuestions)
                    } else {
                        callback.invoke(emptyList())
                    }
                } catch (e: JSONException) {
                    requireActivity().runOnUiThread {
                        hideLoadingView()
                        try {
                            findNavController().navigate(R.id.action_contentFragment_to_errorFragment)
                        } catch (exception: IllegalArgumentException) {
                            exception.printStackTrace()
                        }
                    }
                    callback.invoke(emptyList())
                }
            }
        })
    }

    private fun showLoadingView() {
        loadingView.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        loadingView.visibility = View.GONE
    }
}
