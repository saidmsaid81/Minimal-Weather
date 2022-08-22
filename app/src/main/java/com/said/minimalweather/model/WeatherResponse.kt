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

package com.said.minimalweather.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: Hourly,
    val daily: Daily
)

data class Hourly(
    val data: List<CurrentWeatherData>
)

data class Daily(
    val data: List<ForecastWeatherData>
)

@Entity(tableName = "current_weather")
data class CurrentWeatherData(
    @PrimaryKey val time: Int,
   val summary: String,
    val temperature: Double,
    val icon: String
)

@Entity(tableName = "forecast_weather")
data class ForecastWeatherData(
    @PrimaryKey val time: Int,
    val summary: String,
    @SerializedName("temperatureHigh") val maximumTemperature: Double,
    @SerializedName("temperatureLow") val minimumTemperature: Double,
    val icon: String
)