package com.example.knowitall.screens.play

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.knowitall.MainActivity
import com.example.knowitall.R
import com.example.knowitall.databinding.FragmentPlayBinding
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class PlayFragment : Fragment() {
    private lateinit var binding: FragmentPlayBinding
    private lateinit var retrofit: Retrofit
    private lateinit var wikiApi: WikiApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playButton.setOnClickListener {
            showLoadingView()
            fetchRandomContent()
        }
    }

    private fun showLoadingView() {
        binding.loadingView.visibility = View.VISIBLE
        binding.playButton.isEnabled = false
    }

    private fun hideLoadingView() {
        binding.loadingView.visibility = View.GONE
        binding.playButton.isEnabled = true
    }

    private fun fetchRandomContent() {
        retrofit = Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        wikiApi = retrofit.create(WikiApiService::class.java)

        wikiApi.fetchRandomContent().enqueue(object : Callback<RandomContentResponse> {
            override fun onFailure(call: Call<RandomContentResponse>, t: Throwable) {
                requireActivity().runOnUiThread {
                    hideLoadingView()
                    findNavController().navigate(R.id.action_playFragment_to_errorFragment)
                }
            }

            override fun onResponse(call: Call<RandomContentResponse>, response: Response<RandomContentResponse>) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.query?.pages?.values?.first()
                    val titleAndContent = responseData?.let { parseResponse(it) }

                    if ((titleAndContent?.content?.length ?: 0) >= 1500) {
                        // Update UI on the main thread
                        requireActivity().runOnUiThread {
                            hideLoadingView() // Hide the loading view before navigating
                            val bundle = Bundle().apply {
                                putString("title", titleAndContent?.title)
                                putString("content", titleAndContent?.content)
                            }
                            findNavController().navigate(R.id.action_playFragment_to_contentFragment, bundle)
                        }
                    } else {
                        fetchRandomContent()
                    }
                } else {
                    onFailure(call, Throwable("Unsuccessful response"))
                }
            }
        })
    }

    private fun parseResponse(responseData: RandomContentResponseData): TitleAndContent {
        val title = responseData.title
        val content = responseData.extract

        return TitleAndContent(title, content)
    }

    private data class TitleAndContent(val title: String, val content: String)

    interface WikiApiService {
        @GET("/w/api.php?action=query&format=json&generator=random&grnnamespace=0&grnlimit=1&prop=extracts&exintro=1&explaintext=1")
        fun fetchRandomContent(): Call<RandomContentResponse>
    }

    data class RandomContentResponse(
        @SerializedName("query") val query: RandomContentResponseQuery
    )

    data class RandomContentResponseQuery(
        @SerializedName("pages") val pages: Map<String, RandomContentResponseData>
    )

    data class RandomContentResponseData(
        @SerializedName("title") val title: String,
        @SerializedName("extract") val extract: String
    )
}