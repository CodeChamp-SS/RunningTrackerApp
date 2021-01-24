package com.saarthak.runningtrackerapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saarthak.runningtrackerapp.model.Run
import com.saarthak.runningtrackerapp.repository.MainRepo
import com.saarthak.runningtrackerapp.util.SortType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(val mainRepo: MainRepo): ViewModel() {

    private val runsSorted_date = mainRepo.getRunsSortedByTimestamp()
    private val runsSorted_dist = mainRepo.getRunsSortedByDist()
    private val runsSorted_cal = mainRepo.getRunsSortedByCaloriesBurnt()
    private val runsSorted_avgSpeed = mainRepo.getRunsSortedByAvgSpeed()
    private val runsSorted_timeMS = mainRepo.getRunsSortedByTime_ms()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        runs.addSource(runsSorted_date) {
            if(sortType == SortType.DATE){
                it?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSorted_timeMS) {
            if(sortType == SortType.RUN_TIME){
                it?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSorted_cal) {
            if(sortType == SortType.CAL_BURNT){
                it?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSorted_dist) {
            if(sortType == SortType.DIST){
                it?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSorted_avgSpeed) {
            if(sortType == SortType.AVG_SPEED){
                it?.let {
                    runs.value = it
                }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.DATE -> runsSorted_date.value?.let { runs.value = it }

        SortType.AVG_SPEED -> runsSorted_avgSpeed.value?.let { runs.value = it }

        SortType.CAL_BURNT -> runsSorted_cal.value?.let { runs.value = it }

        SortType.DIST -> runsSorted_dist.value?.let { runs.value = it }

        SortType.RUN_TIME -> runsSorted_timeMS.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepo.insertRun(run)
        delay(1500)
    }

}