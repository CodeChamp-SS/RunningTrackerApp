package com.saarthak.runningtrackerapp.util

import android.graphics.Color

object Constants {
    const val DB_NAME = "RunningDB"
    const val REQ_CODE_LOCN_PERM = 1

    const val ACTION_START_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    // set it to atleast 1 i.e, don't put it to be = 0 to avoid error
    const val NOTIFICATION_ID = 2

    const val LOCN_UPDATE_INTERVAL = 4000L
    const val FASTEST_UPDATE_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f

    const val MAP_ZOOM = 15f

    const val TIMER_DELAY = 50L

    const val SHARED_PREF_NAME = "shared_pref"
    const val KEY_FIRST_TIME = "KEY_FIRST_TIME"
    const val KEY_NAME = "NAME"
    const val KEY_WT = "WT"
}