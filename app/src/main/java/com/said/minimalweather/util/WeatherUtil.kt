/*
 * Copyright (c) 2022 Said Mohamed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.said.minimalweather.util

import android.content.Context
import android.net.ConnectivityManager
import com.said.minimalweather.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


fun getIconResId(icon: String): Int {
    val iconResId: Int
    when (icon) {
        "clear-day" -> iconResId = R.drawable.ic_wi_sunny
        "partly-cloudy-day" -> iconResId = R.drawable.partly_cloudy_day
        "cloudy" -> iconResId = R.drawable.ic_wi_cloudy
        "fog" -> iconResId = R.drawable.ic_wi_fog
        "hail" -> iconResId = R.drawable.ic_wi_showers
        "thunderstorm" -> iconResId = R.drawable.ic_wi_thunderstorm
        "rain" -> iconResId = R.drawable.ic_wi_rain
        "snow" -> iconResId = R.drawable.ic_wi_snow
        "sleet" -> iconResId = R.drawable.sleet
        "strong-wind" -> iconResId = R.drawable.wind
        "tornado" -> iconResId = R.drawable.tornado
        "clear-night" -> iconResId = R.drawable.ic_wi_night_clear
        "partly-cloudy-night" -> iconResId = R.drawable.ic_wi_night_alt_cloudy
        else -> iconResId = R.drawable.ic_na
    }
    return iconResId
}

fun calculateTemperature(temperature: Double): Double {

    //Convert the temperature in Celsius to fahrenheit and return the value to 1 decimal place
    val tempFahrenheit = temperature * 9 / 5 + 32
    val newFormat = DecimalFormat("#.#")
    return java.lang.Double.valueOf(newFormat.format(tempFahrenheit))


}

fun getTodaysDate(): Int {
    return SimpleDateFormat("dd", Locale.ENGLISH).format(System.currentTimeMillis()).toInt()
}

fun convertUnixToDay(timeInSeconds: Long): String? {
    val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val dateFormat = Date(timeInSeconds * 1000)
    return simpleDateFormat.format(dateFormat)

}

fun isDeviceOffline(context: Context?): Boolean {
    if (context != null) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo == null ||
                !connectivityManager.activeNetworkInfo!!.isConnected
    }
    return true

}