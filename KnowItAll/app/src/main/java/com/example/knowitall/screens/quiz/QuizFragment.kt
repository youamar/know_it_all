package com.example.knowitall.screens.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowitall.MainActivity
import com.example.knowitall.R
import com.example.knowitall.screens.login.LoginViewModel
import com.example.knowitall.screens.login.LoginViewModelFactory
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

class QuizFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var submitButton: Button
    private lateinit var openAiApi: OpenAiApi
    private val selectedAnswers = mutableMapOf<Int, String>()
    private var questions: ArrayList<String>? = null
    private var questionIndex: Int = 0
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loadingView: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        loadingView = view.findViewById(R.id.loadingView)
        recyclerView = view.findViewById(R.id.quizRecyclerView)
        submitButton = view.findViewById(R.id.submitButton)
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

        // Assign the questions to the member variable
        questions = arguments?.getStringArrayList("questions")

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(requireContext())).get(LoginViewModel::class.java)

        // Check if the questions are available
        if (questions != null) {
            val adapter = QuizAdapter(questions!!, object : QuizAdapter.AnswerSelectionListener {
                override fun onAnswerSelected(questionIndex: Int, selectedAnswer: String) {
                    selectedAnswers[questionIndex] = selectedAnswer
                }
            }, questionIndex)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            submitButton.setOnClickListener {
                showLoadingView()
                val content = adapter.getContent()
                getCorrectAnswers(content) { correctAnswers ->
                    val cleanedAnswers = correctAnswers.mapIndexedNotNull { index, answer ->
                        val startIndex = answer.indexOf(".")
                        if (startIndex != -1) {
                            val cleanedAnswer = answer.substring(startIndex + 2)
                            if (index == 0) {
                                cleanedAnswer.removePrefix("Answer:")
                            } else {
                                cleanedAnswer.replace(Regex("^\\d+\\.\\s*"), "")
                            }
                        } else {
                            null
                        }
                    }
                    val allSelectedAnswers = adapter.getSelectedAnswerAndQuestion()
                    val uniqueAnswers = mutableListOf<List<String>>()
                    val nbQuestions = adapter.getNbQuestions()

                    // Iterate over the answers from the back
                    for (i in allSelectedAnswers.size - 1 downTo 0) {
                        val answer = allSelectedAnswers[i]
                        val firstElement = answer[0]

                        // Check if the answer with the same first element already exists in uniqueAnswers
                        val exists = uniqueAnswers.any { it[0] == firstElement }

                        if (!exists) {
                            // Add the answer to uniqueAnswers if it's not a duplicate
                            uniqueAnswers.add(answer)
                        }
                    }
                    // Reverse uniqueAnswers to maintain the original order
                    uniqueAnswers.reverse()
                    uniqueAnswers.sortBy { it[0] }
                    val ans = uniqueAnswers.map { it[1] }


                    if (cleanedAnswers.size < nbQuestions) {
                        // Resend the request to the API
                        getCorrectAnswers(content) { updatedCorrectAnswers ->
                            updatedCorrectAnswers.isNotEmpty()
                            hideLoadingView()
                            findNavController().navigate(R.id.action_quizFragment_to_errorFragment)
                        }
                    } else {
                        hideLoadingView()
                        findNavController().navigate(R.id.action_quizFragment_to_sentFragment)
                    }
                    val xp = calculateXP(ans, cleanedAnswers)
                    updateLatestUserXP(xp)
                }
            }

        }
    }

    private fun showLoadingView() {
        loadingView.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        loadingView.visibility = View.GONE
    }

    private fun calculateXP(selectedAnswers: List<String>, correctAnswers: List<String>): Int {
        var xp = 0
        for (i in selectedAnswers.indices) {
            if (i < correctAnswers.size) {  // Check if the index is within bounds
                val cleanedAnswerWithoutSpaces = correctAnswers[i].replace(" ", "")
                val ansWithoutSpaces = selectedAnswers[i].replace(" ", "")

                if (ansWithoutSpaces == cleanedAnswerWithoutSpaces) {
                    xp += 10
                }
            }
        }
        return xp
    }

    private fun updateLatestUserXP(xp: Int) {
        val latestLogin = loginViewModel.getLatestLogin()
        if (latestLogin != null) {
            latestLogin.xp += xp
            loginViewModel.updateUserLogin(latestLogin)
        }
    }

    private fun getCorrectAnswers(content: String, callback: (List<String>) -> Unit) {
        val prompt = "Give me a list of the good answers containing only the good answer for each question based on the exact" +
                "following content without repeating the question itself (meaning don't add any words to the answers) example : A. This B. That:\n$content"
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
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.choices?.let {
                        val answers = it[0].text.split("\n").filter { answer ->
                            answer.trim().isNotEmpty()
                        }
                        callback(answers)
                    }
                } else {
                    findNavController().navigate(R.id.action_quizFragment_to_errorFragment)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                findNavController().navigate(R.id.action_quizFragment_to_errorFragment)
            }
        })
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