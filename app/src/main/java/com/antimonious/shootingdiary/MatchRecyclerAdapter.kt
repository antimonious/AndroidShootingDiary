package com.antimonious.shootingdiary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MatchRecyclerAdapter(private val clickListener: (String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var matches: List<Match> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return MatchViewHolder (
            LayoutInflater.from(
                parent.context)
                .inflate(
                    R.layout.match_item,
                    parent,
                    false)) {
            clickListener(matches[it].Id.toString())
        }
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
        itemView: View,
        clickAtPosition: (Int) -> Unit
    ): RecyclerView.ViewHolder(itemView) {
        private val matchDate: TextView =
            itemView.findViewById(R.id.matchItemDateView)
        private val matchTimeSpan: TextView =
            itemView.findViewById(R.id.matchItemTimeSpanView)
        private val matchLocation: TextView =
            itemView.findViewById(R.id.matchItemLocationView)
        private val matchScore: TextView =
            itemView.findViewById(R.id.matchItemScoreView)

        init {
            itemView.setOnClickListener {
                clickAtPosition(adapterPosition)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind (match: Match) {
            matchDate.text = match.Date
            matchTimeSpan.text = "${match.StartTime} - ${match.EndTime}"
            matchLocation.text = match.Location
            matchScore.text = match.Result.toString()
        }
    }
}