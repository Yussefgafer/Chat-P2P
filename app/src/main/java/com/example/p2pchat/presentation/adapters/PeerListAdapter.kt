package com.example.p2pchat.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.p2pchat.R

class PeerListAdapter(
    private val onPeerClick: (String) -> Unit
) : RecyclerView.Adapter<PeerListAdapter.PeerViewHolder>() {

    private var peers: List<String> = emptyList()

    fun updatePeers(newPeers: List<String>) {
        peers = newPeers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return PeerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeerViewHolder, position: Int) {
        holder.bind(peers[position])
    }

    override fun getItemCount(): Int = peers.size

    inner class PeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(peer: String) {
            textView.text = peer
            itemView.setOnClickListener {
                onPeerClick(peer)
            }
        }
    }
}
