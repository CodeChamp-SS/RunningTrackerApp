package com.saarthak.runningtrackerapp.ui

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.saarthak.runningtrackerapp.model.Run
import com.saarthak.runningtrackerapp.util.Utilities
import kotlinx.android.synthetic.main.marker_items.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(val runs: List<Run>, ctx: Context, layoutId: Int): MarkerView(ctx, layoutId) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        if(e == null) return

        val id = e.x.toInt()
        val run = runs[id]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

        date_tv.text = sdf.format(calendar.time)

        val avgSpeed = "${run.avgSpeed_kmph} kmph"
        totAvgSpeed_tv.text = avgSpeed

        val dist_km = "${run.dist_m / 1000f} km"
        totDist_tv.text = dist_km

        totT_tv.text = Utilities.getFormattedTime(run.time_ms)

        val calBurnt = "${run.caloriesBurnt} kcal"
        totCal_tv.text = calBurnt
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, (-height).toFloat())
    }

}