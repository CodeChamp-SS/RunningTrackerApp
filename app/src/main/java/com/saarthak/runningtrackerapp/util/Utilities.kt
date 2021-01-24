package com.saarthak.runningtrackerapp.util

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object Utilities {

    fun locnPermissionsCheck(context: Context) =
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        } else{
            EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

    fun getFormattedTime(ms: Long, includeMs: Boolean = false): String{
        var millS = ms
        val hrs = TimeUnit.MILLISECONDS.toHours(millS)
        millS -= TimeUnit.HOURS.toMillis(hrs)
        val mins = TimeUnit.MILLISECONDS.toMinutes(millS)
        millS -= TimeUnit.MINUTES.toMillis(mins)
        val secs = TimeUnit.MILLISECONDS.toSeconds(millS)
        millS -= TimeUnit.SECONDS.toMillis(secs)
        millS /= 10 // as we need ms rounded to 2 places

        return if(! includeMs){
            "${if(hrs < 10) "0" else ""}$hrs : ${if(mins < 10) "0" else ""}$mins : ${if(secs < 10) "0" else ""}$secs"
        } else{
            "${if(hrs < 10) "0" else ""}$hrs : ${if(mins < 10) "0" else ""}$mins : ${if(secs < 10) "0" else ""}$secs : ${if(millS < 10) "0" else ""}$millS"
        }
    }

    fun calcDist(coord: Coord): Float{
        var res = 0f
        for(i in 0..coord.size - 2){
            val pos1 = coord[i]
            val pos2 = coord[i+1]

            val arr = FloatArray(1)
            Location.distanceBetween(pos1.latitude, pos1.longitude,
                pos2.latitude, pos2.longitude, arr)

            res += arr[0]
        }

        return res
    }
}