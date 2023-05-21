package com.example.knowitall.screens.quiz

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.knowitall.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class ContentFragment : Fragment() {
    private lateinit var contentText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_content, container, false)
        contentText = view.findViewById(R.id.contentText)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title")
        val content = arguments?.getString("content")

        val combinedText = "$title\n\n$content"

        contentText.text = combinedText

        val startButton = view.findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            generateQuestions(title, content, 5) // Generate 5 questions
        }
    }

    private fun generateQuestions(title: String?, content: String?, numQuestions: Int) {
        val client = OkHttpClient()
        val url = "https://api-inference.huggingface.co/models/voidful/context-only-question-generator"

        val json = """
            {
                "context": "$content",
                "num_questions": $numQuestions
            }
        """.trimIndent()

        for (i in 0 until numQuestions) {
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer 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")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Handle request failure
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("ContentFragment", "Response: $responseBody")

                    try {
                        val responseObject = JSONArray(responseBody)

                        if (responseObject.length() > 0) {
                            val questionObject = responseObject.getJSONObject(0)
                            val question = questionObject.getString("question")
                            Log.d("ContentFragment", "Question ${i + 1}: $question")
                        } else {
                            // Handle the case when no questions are generated
                            Log.d("ContentFragment", "No questions generated")
                        }
                    } catch (e: JSONException) {
                        // Handle JSON parsing error
                    }
                }
            })

            // Add a delay between each API call
            Thread.sleep(1000) // Adjust the delay time as needed
        }
    }

}