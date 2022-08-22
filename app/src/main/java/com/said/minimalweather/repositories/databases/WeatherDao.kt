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

package com.said.minimalweather.repositories.databases

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.said.minimalweather.model.CurrentWeatherData
import com.said.minimalweather.model.AppSettings
import com.said.minimalweather.model.ForecastWeatherData

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCurrentWeather(currentWeather: List<CurrentWeatherData>)

    @Query("SELECT * FROM current_weather WHERE time <= :time ORDER BY time DESC ")
    fun getCurrentWeather(time: Long): LiveData<CurrentWeatherData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addForecastWeather(forecastWeatherList: List<ForecastWeatherData>)

    @Query("SELECT * FROM forecast_weather WHERE time > :time ORDER BY time ASC LIMIT 3")
    fun getForecastWeatherList(time: Long): LiveData<List<ForecastWeatherData>>

    @Query("Select * from app_settings")
    suspend fun getSettingsValue(): AppSettings?

    @Query("Select * from app_settings")
    fun getSettings(): LiveData<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSettings(settings: AppSettings)

    @Query("UPDATE app_settings SET isCelsius =:isCelsius")
    suspend fun updateTemperatureUnit(isCelsius: Boolean)

    @Query("UPDATE app_settings SET lastFetch =:day")
    suspend fun updateLastFetch(day: Int)

    @Query("DELETE FROM current_weather")
    suspend fun deleteCurrentWeatherData()

    @Query("DELETE FROM forecast_weather")
    suspend fun deleteForecastWeatherData()


}