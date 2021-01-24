package com.saarthak.runningtrackerapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.saarthak.runningtrackerapp.room.RunDB
import com.saarthak.runningtrackerapp.util.Constants.DB_NAME
import com.saarthak.runningtrackerapp.util.Constants.KEY_FIRST_TIME
import com.saarthak.runningtrackerapp.util.Constants.KEY_NAME
import com.saarthak.runningtrackerapp.util.Constants.KEY_WT
import com.saarthak.runningtrackerapp.util.Constants.SHARED_PREF_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunDb(@ApplicationContext context: Context) = Room.databaseBuilder(context, RunDB::class.java, DB_NAME).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunDB) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context) = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) = sharedPreferences.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWt(sharedPreferences: SharedPreferences) = sharedPreferences.getFloat(KEY_WT, 75f)

    @Singleton
    @Provides
    fun provideFirstTime(sharedPreferences: SharedPreferences) = sharedPreferences.getBoolean(
        KEY_FIRST_TIME, true)

}