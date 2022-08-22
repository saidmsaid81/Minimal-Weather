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

package com.said.minimalweather.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.said.minimalweather.model.AppSettings
import com.said.minimalweather.model.CurrentWeatherData
import com.said.minimalweather.model.ForecastWeatherData
import com.said.minimalweather.model.GeocodingData
import com.said.minimalweather.repositories.WeatherRepository

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val mWeatherRepository = WeatherRepository.getInstance(application, viewModelScope)

    fun getCurrentWeather(latitude: Float, longitude: Float): LiveData<CurrentWeatherData?> =
            mWeatherRepository.getCurrentWeather(latitude, longitude)

    fun getForecastDataList(): LiveData<List<ForecastWeatherData>> = mWeatherRepository.getForecastList()

    fun getSettings(): LiveData<AppSettings?> = mWeatherRepository.getSettings()

    fun fetchGeocodingData(address: String) = mWeatherRepository.fetchGeocodingData(address)

    fun getGeocodingData(): LiveData<GeocodingData?> = mWeatherRepository.mData

    fun updateSettings(settings: AppSettings) = mWeatherRepository.updateSettings(settings)

    fun updateTemperatureUnits(isCelsius: Boolean) = mWeatherRepository.updateTemperatureUnit(isCelsius)

}