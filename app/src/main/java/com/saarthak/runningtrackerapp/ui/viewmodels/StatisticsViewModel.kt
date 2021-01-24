package com.saarthak.runningtrackerapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.saarthak.runningtrackerapp.repository.MainRepo

class StatisticsViewModel @ViewModelInject constructor(val mainRepo: MainRepo): ViewModel() {

    val totalDist = mainRepo.getTotalDist()
    val totalTime_ms = mainRepo.getTotalTime_ms()
    val totalAvgSpeed = mainRepo.getTotalAvgSpeed()
    val totalCalBurnt = mainRepo.getTotalCaloriesBurnt()

    val runsSorted_date = mainRepo.getRunsSortedByTimestamp()

}