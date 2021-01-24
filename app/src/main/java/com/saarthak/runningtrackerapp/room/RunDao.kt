package com.saarthak.runningtrackerapp.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.saarthak.runningtrackerapp.model.Run

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("select * from RunningTable order by timeStamp desc")
    fun getAllRuns_byTimeStamp(): LiveData<List<Run>>

    @Query("select * from RunningTable order by time_ms desc")
    fun getAllRuns_byTime(): LiveData<List<Run>>

    @Query("select * from RunningTable order by dist_m desc")
    fun getAllRuns_byDist_m(): LiveData<List<Run>>

    @Query("select * from RunningTable order by caloriesBurnt desc")
    fun getAllRuns_byCaloriesBurnt(): LiveData<List<Run>>

    @Query("select * from RunningTable order by avgSpeed_kmph desc")
    fun getAllRuns_byAvgSpeed(): LiveData<List<Run>>

    @Query("select sum(time_ms) from RunningTable")
    fun getTotalTime_ms(): LiveData<Long>

    @Query("select sum(caloriesBurnt) from RunningTable")
    fun getTotalCaloriesBurnt(): LiveData<Long>

    @Query("select sum(dist_m) from RunningTable")
    fun getTotalDist(): LiveData<Long>

    @Query("select avg(avgSpeed_kmph) from RunningTable")
    fun getTotalAvgSpeed(): LiveData<Float>

}