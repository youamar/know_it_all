package com.example.knowitall.screens.museums

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knowitall.R

class MuseumAdapter : RecyclerView.Adapter<MuseumAdapter.MuseumViewHolder>() {

    private var museumsList: List<Museum> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuseumViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_museum, parent, false)
        return MuseumViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MuseumViewHolder, position: Int) {
        val museum = museumsList[position]
        holder.bind(museum)
    }

    override fun getItemCount(): Int {
        return museumsList.size
    }

    fun submitList(list: List<Museum>) {
        museumsList = list
        notifyDataSetChanged()
    }

    inner class MuseumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewAddress: TextView = itemView.findViewById(R.id.textViewAddress)

        fun bind(museum: Museum) {
            textViewName.text = museum.name
            textViewAddress.text = museum.address
        }
    }

    data class Museum(
        val name: String,
        val address: String
    )
}
