package com.antimonious.shootingdiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SeriesRecyclerAdapter(private val clickListener: (String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var series: List<Series> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            RecyclerView.ViewHolder {
        return SeriesViewHolder (
            LayoutInflater.from(
                parent.context)
                .inflate(
                    R.layout.series_item,
                    parent,
                    false)) {
            clickListener(series[it].Id.toString())
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SeriesViewHolder ->
                holder.bind(series[position])
        }
    }

    override fun getItemCount(): Int {
        return series.size
    }

    fun postItemsList(data: ArrayList<Series>) {
        series = data
    }

    class SeriesViewHolder constructor(
        itemView: View,
        clickAtPosition: (Int) -> Unit
    ): RecyclerView.ViewHolder(itemView) {
        private val seriesId: TextView =
            itemView.findViewById(R.id.seriesItemSeriesNumberView)
        private val seriesTimeSpan: TextView =
            itemView.findViewById(R.id.seriesItemTimeSpanView)
        private val seriesInner10s: TextView =
            itemView.findViewById(R.id.seriesItemInner10sView)
        private val seriesScore: TextView =
            itemView.findViewById(R.id.seriesItemScoreView)

        init {
            itemView.setOnClickListener {
                clickAtPosition(adapterPosition)
            }
        }

        fun bind (series: Series) {
            "Series: ${series.Id}".also { seriesId.text = it }
            seriesTimeSpan.text = series.TimeSpan
            seriesInner10s.text = series.Inner10s.toString()
            seriesScore.text = series.Result.toString()
        }
    }
}