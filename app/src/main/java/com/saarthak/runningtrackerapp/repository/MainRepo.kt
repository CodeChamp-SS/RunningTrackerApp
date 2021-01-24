package com.saarthak.runningtrackerapp.repository

import com.saarthak.runningtrackerapp.model.Run
import com.saarthak.runningtrackerapp.room.RunDao
import javax.inject.Inject

class MainRepo @Inject constructor(val runDao: RunDao) {

    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getRunsSortedByTimestamp() = runDao.getAllRuns_byTimeStamp()

    fun getRunsSortedByTime_ms() = runDao.getAllRuns_byTime()

    fun getRunsSortedByDist() = runDao.getAllRuns_byDist_m()

    fun getRunsSortedByAvgSpeed() = runDao.getAllRuns_byAvgSpeed()

    fun getRunsSortedByCaloriesBurnt() = runDao.getAllRuns_byCaloriesBurnt()

    fun getTotalDist() = runDao.getTotalDist()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun getTotalTime_ms() = runDao.getTotalTime_ms()

    fun getTotalCaloriesBurnt() = runDao.getTotalCaloriesBurnt()

}