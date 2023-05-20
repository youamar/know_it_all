package com.example.knowitall.screens.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowitall.R
import com.example.knowitall.database.LoginDatabase

class LeaderboardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var database: LoginDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        recyclerView = view.findViewById(R.id.leaderboardRecyclerView)
        adapter = LeaderboardAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        database = LoginDatabase.getInstance(requireContext())

        val leaderboardLiveData = database.userLoginDao.getLeaderboard()
        leaderboardLiveData.observe(viewLifecycleOwner) { leaderboard ->
            adapter.submitList(leaderboard)
        }

        return view
    }
}
