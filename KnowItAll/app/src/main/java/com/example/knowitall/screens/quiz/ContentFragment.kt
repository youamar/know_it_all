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
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class ContentFragment : Fragment() {
    private lateinit var contentText: TextView
    private lateinit var questions: List<String>
    private var numQuestions = 5
    private var numAnswers = 3
    private lateinit var loadingView: RelativeLayout
    private lateinit var openAiApi: OpenAiApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_content, container, false)
        contentText = view.findViewById(R.id.contentText)
        loadingView = view.findViewById(R.id.loadingView)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()

        openAiApi = retrofit.create(OpenAiApi::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).showLoginFragment()
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

        val call = openAiApi.generateCompletions(
            "Bearer ${getString(R.string.random_string3)}",
            requestBody
        )

        call.enqueue(object : Callback<ApiResponse> {
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
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

            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    val choices = apiResponse.choices
                    if (choices.isNotEmpty()) {
                        val generatedQuestions = mutableListOf<String>()
                        for (choice in choices) {
                            val question = choice.text
                            generatedQuestions.add(question)
                        }
                        callback.invoke(generatedQuestions)
                    } else {
                        callback.invoke(emptyList())
                    }
                } else {
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
    interface OpenAiApi {
        @POST("completions")
        fun generateCompletions(
            @Header("Authorization") authorization: String,
            @Body requestBody: RequestBody
        ): Call<ApiResponse>
    }
    data class ApiResponse(
        @SerializedName("choices")
        val choices: List<Choice>
    )

    data class Choice(
        @SerializedName("text")
        val text: String
    )
}
