package com.example.knowitall.screens.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.knowitall.R
import com.example.knowitall.database.UserLogin

class LeaderboardAdapter : ListAdapter<UserLogin, LeaderboardAdapter.LeaderboardViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val userLogin = getItem(position)
        val positionNumber = position + 1
        val emailWithPosition = "$positionNumber. ${userLogin.email}"
        holder.bind(emailWithPosition, userLogin.xp)
    }

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail)
        private val textViewXp: TextView = itemView.findViewById(R.id.textViewXp)

        fun bind(email: String, xp: Int) {
            val emailParts = email.split("@")
            val username = emailParts.first()

            val emailWithPosition = "$username"
            val xpText = "$xp XP"

            textViewEmail.text = emailWithPosition
            textViewXp.text = xpText
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UserLogin>() {
        override fun areItemsTheSame(oldItem: UserLogin, newItem: UserLogin): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: UserLogin, newItem: UserLogin): Boolean {
            return oldItem == newItem
        }
    }
}