package com.antimonious.shootingdiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MatchRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var matches: List<Match> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return MatchViewHolder (
            LayoutInflater.from(
                parent.context)
                .inflate(
                    R.layout.match_item,
                    parent,
                    false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MatchViewHolder ->
                holder.bind(matches[position])
        }
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    fun postItemsList(data: ArrayList<Match>) {
        matches = data
    }

    class MatchViewHolder constructor (
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        private val matchDate: TextView =
            itemView.findViewById(R.id.matchItemDateView)
        private val matchTimeSpan: TextView =
            itemView.findViewById(R.id.matchItemTimeSpanView)
        private val matchLocation: TextView =
            itemView.findViewById(R.id.matchItemLocationView)
        private val matchScore: TextView =
            itemView.findViewById(R.id.matchItemScoreView)

        fun bind (match: Match) {
            matchDate.text = match.Date
            matchTimeSpan.text = match.TimeSpan
            matchLocation.text = match.Location
            matchScore.text = match.Result
        }
    }
}