package com.saarthak.runningtrackerapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.saarthak.runningtrackerapp.model.Run

@Database(entities = [Run::class], version = 1)
@TypeConverters(TypeConverter::class)

abstract class RunDB: RoomDatabase() {

    abstract fun getRunDao(): RunDao

}