package com.example.knowitall.screens.play

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.knowitall.R
import com.example.knowitall.databinding.FragmentPlayBinding

class PlayFragment : Fragment() {
    private lateinit var binding: FragmentPlayBinding
    private lateinit var viewModel: PlayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)

        viewModel.randomContent.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { randomContent ->
                handleRandomContent(randomContent)
            }
        })

        binding.playButton.setOnClickListener {
            showLoadingView()
            val context = requireContext().applicationContext
            viewModel.fetchRandomContent(context)
        }

        viewModel.navigateToErrorFragment.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                navigateToErrorFragment()
            }
        })
    }

    private fun navigateToErrorFragment() {
        val navController = findNavController()
        navController.navigate(R.id.action_playFragment_to_errorFragment)
    }

    private fun showLoadingView() {
        binding.loadingView.visibility = View.VISIBLE
        binding.playButton.isEnabled = false
    }

    private fun hideLoadingView() {
        binding.loadingView.visibility = View.GONE
        binding.playButton.isEnabled = true
    }

    private fun handleRandomContent(randomContent: TitleAndContent) {
        if (randomContent.content.length >= 1500) {
            hideLoadingView()
            val bundle = Bundle().apply {
                putString("title", randomContent.title)
                putString("content", randomContent.content)
            }
            findNavController().navigate(R.id.action_playFragment_to_contentFragment, bundle)
        } else {
            viewModel.fetchRandomContent(requireContext().applicationContext)
        }
    }

    data class TitleAndContent(val title: String, val content: String)
}
