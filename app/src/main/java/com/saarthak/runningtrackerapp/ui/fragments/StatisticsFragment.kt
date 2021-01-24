package com.saarthak.runningtrackerapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.ui.CustomMarkerView
import com.saarthak.runningtrackerapp.ui.viewmodels.StatisticsViewModel
import com.saarthak.runningtrackerapp.util.Utilities
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()

    private fun subscribeToObservers(){
        viewModel.totalTime_ms.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totTime = Utilities.getFormattedTime(it)
                totalTime_tv.text = totTime
            }
        })

        viewModel.totalDist.observe(viewLifecycleOwner, Observer {
            it?.let {
                val d_km = it / 1000f
                val totDist = round(d_km * 10f) / 10f
                totalDistance_tv.text = "$totDist km"
            }
        })

        viewModel.totalCalBurnt.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totCal = round(it * 100f) / 100f
                totalCal_tv.text = "$totCal kcal"
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                avgSpeed_tv.text = "$avgSpeed km/h"
            }
        })

        viewModel.runsSorted_date.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = it.indices.map { i ->
                    BarEntry(i.toFloat(), it[i].avgSpeed_kmph)
                }
                val barData = BarDataSet(avgSpeed, "Average Speed").apply {
                    valueTextColor = Color.BLUE
                    color = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                }

                barChart.data = BarData(barData)
                barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_items)
                barChart.invalidate()
            }
        })
    }

    private fun setUpBarChart(){
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.BLACK
            textColor = Color.BLUE
        }

        barChart.axisLeft.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLUE
        }

        barChart.axisRight.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLUE
        }

        barChart.apply {
            description.text = "Average Speed over time"
            legend.isEnabled = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        setUpBarChart()
    }

}