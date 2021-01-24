package com.saarthak.runningtrackerapp.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.saarthak.runningtrackerapp.R
import com.saarthak.runningtrackerapp.ui.MainActivity
import com.saarthak.runningtrackerapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideFusedLocnClient(@ApplicationContext ctx: Context) = FusedLocationProviderClient(ctx)

    @Provides
    @ServiceScoped
    fun provideMainPendintIntent(@ApplicationContext ctx: Context) = PendingIntent.getActivity(ctx, 0,
        Intent(ctx, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT)

    @Provides
    @ServiceScoped
    fun provideNotificationBuilder(@ApplicationContext ctx: Context, pendingIntent: PendingIntent) = NotificationCompat.Builder(ctx,
        Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true) // so that it can't be swiped away
        .setSmallIcon(R.drawable.ic_run)
        .setContentTitle("Running App")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)
}