package com.example.knowitall.screens.play

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.knowitall.R
import com.example.knowitall.databinding.FragmentPlayBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PlayFragment : Fragment() {
    private lateinit var binding: FragmentPlayBinding

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
            showLoadingView() // Show the loading view before fetching the content
            fetchRandomContent()
        }
    }

    private fun showLoadingView() {
        // Show the loading view, hide other views if necessary
        binding.loadingView.visibility = View.VISIBLE
        binding.playButton.isEnabled = false // Disable the play button while loading
    }

    private fun hideLoadingView() {
        // Hide the loading view, show other views if necessary
        binding.loadingView.visibility = View.GONE
        binding.playButton.isEnabled = true // Enable the play button after loading
    }

    private fun fetchRandomContent() {
        val url = "https://en.wikipedia.org/w/api.php?action=query&format=json&generator=random&grnnamespace=0&grnlimit=1&prop=extracts&exintro=1&explaintext=1"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                hideLoadingView() // Hide the loading view in case of failure
                // Handle the request failure
            }

            // Inside onResponse method of fetchRandomContent function
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val titleAndContent = parseResponse(responseData)

                if (titleAndContent.second.length >= 1500) {
                    // Update UI on the main thread
                    requireActivity().runOnUiThread {
                        hideLoadingView() // Hide the loading view before navigating
                        val bundle = Bundle().apply {
                            putString("title", titleAndContent.first)
                            putString("content", titleAndContent.second)
                        }
                        findNavController().navigate(R.id.action_playFragment_to_contentFragment, bundle)
                    }
                } else {
                    // Content is not long enough, fetch again
                    fetchRandomContent()
                }
            }
        })
    }

    private fun parseResponse(responseData: String?): Pair<String, String> {
        val jsonObject = JSONObject(responseData)
        val pages = jsonObject.getJSONObject("query").getJSONObject("pages")
        val pageId = pages.keys().next()
        val page = pages.getJSONObject(pageId)
        val title = page.getString("title")
        val content = page.getString("extract")

        return Pair(title, content)
    }
}
