package com.saarthak.runningtrackerapp.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "RunningTable")
data class Run (
    var img: Bitmap? = null,
    var timeStamp: Long = 0,
    var avgSpeed_kmph: Float = 0f,
    var dist_m: Long = 0,
    var time_ms: Long = 0,
    var caloriesBurnt: Long = 0
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}