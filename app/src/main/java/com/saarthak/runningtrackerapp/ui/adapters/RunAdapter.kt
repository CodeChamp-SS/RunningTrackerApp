package com.saarthak.runningtrackerapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.model.Run
import com.saarthak.runningtrackerapp.util.Utilities
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter: RecyclerView.Adapter<RunAdapter.ViewHolder>() {

    val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(runs: List<Run>) = differ.submitList(runs)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_run, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val run = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this)
                .load(run.img)
                .into(runImage_iv)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }
            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

            tvDate.text = sdf.format(calendar.time)

            val avgSpeed = "${run.avgSpeed_kmph} kmph"
            tvAvgSpeed.text = avgSpeed

            val dist_km = "${run.dist_m / 1000f} km"
            tvDistance.text = dist_km

            tvTime.text = Utilities.getFormattedTime(run.time_ms)

            val calBurnt = "${run.caloriesBurnt} kcal"
            tvCalories.text = calBurnt
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
}