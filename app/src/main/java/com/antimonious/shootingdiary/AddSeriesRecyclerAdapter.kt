package com.antimonious.shootingdiary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddSeriesRecyclerAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var series: List<Series> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return AddSeriesViewHolder (
            LayoutInflater.from(
                parent.context)
                .inflate(
                    R.layout.add_series_item,
                    parent,
                    false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddSeriesViewHolder ->
                holder.bind(series[position])
        }
    }

    override fun getItemCount(): Int {
        return series.size
    }

    fun postItemsList(data: ArrayList<Series>) {
        series = data
    }

    class AddSeriesViewHolder constructor (
        itemView: View):
            RecyclerView.ViewHolder(itemView) {
                private val seriesId: TextView =
                    itemView.findViewById(R.id.addSeriesId)

            @SuppressLint("SetTextI18n")
            fun bind(series: Series) {
                seriesId.text = "Series ${series.Id}:"
            }
        }
}