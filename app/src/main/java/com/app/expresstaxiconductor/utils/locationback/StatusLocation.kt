package com.app.expresstaxiconductor.utils.locationback

import android.content.Context
import android.preference.PreferenceManager

internal object StatusLocation {

    const val KEY_REQUESTING_LOCATION_UPDATES =
        "request_location_updates"

    fun requestingLocationUpdates(context: Context):Boolean{
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_REQUESTING_LOCATION_UPDATES,true)
    }

    fun setRequestingLocationUpdates(
        context: Context,
        requestingLocationUpdates: Boolean
    ){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply()
    }
}