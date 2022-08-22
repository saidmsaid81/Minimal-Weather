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

package com.said.minimalweather.repositories

import android.app.Application
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.said.minimalweather.model.*
import com.said.minimalweather.repositories.databases.WeatherDao
import com.said.minimalweather.repositories.databases.WeatherDatabase
import com.said.minimalweather.repositories.remote.DARK_SKY_API_KEY
import com.said.minimalweather.repositories.remote.DarkSkyService
import com.said.minimalweather.repositories.remote.GEOCODING_API_KEY
import com.said.minimalweather.repositories.remote.GeocodingService
import com.said.minimalweather.util.getTodaysDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository(
    application: Application,
    private val coroutineScope: CoroutineScope
) {

    private val weatherDao = WeatherDatabase.getDatabase(application).weatherDao()

    fun getCurrentWeather(latitude: Float, longitude: Float): LiveData<CurrentWeatherData?> {
        coroutineScope.launch {
            if (weatherDao.getSettingsValue()?.lastFetch != getTodaysDate()) {
                deleteWeatherData()
                fetchWeatherData(latitude, longitude)
            }
        }
        return weatherDao.getCurrentWeather(System.currentTimeMillis() / 1000)
    }

    fun getForecastList(): LiveData<List<ForecastWeatherData>> {
        return weatherDao.getForecastWeatherList(System.currentTimeMillis() / 1000)
    }

    private suspend fun addCurrentWeather(currentWeather: List<CurrentWeatherData>) {
        weatherDao.addCurrentWeather(currentWeather)
    }

    private suspend fun addListOfForecastData(forecastList: List<ForecastWeatherData>) {
        weatherDao.addForecastWeather(forecastList)
    }

    private fun deleteWeatherData() {
        coroutineScope.launch {
            weatherDao.deleteCurrentWeatherData()
            weatherDao.deleteForecastWeatherData()
        }
    }

    private fun fetchWeatherData(latitude: Float, longitude: Float) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val darkSkyService = Retrofit.Builder()
            .baseUrl("https://api.darksky.net/forecast/$DARK_SKY_API_KEY/$latitude,$longitude/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(DarkSkyService::class.java)

        darkSkyService.getCurrentWeatherData("ca", "currently,minutely,flags")?.enqueue(
            object : Callback<WeatherResponse?> {
                override fun onResponse(
                    call: Call<WeatherResponse?>,
                    response: Response<WeatherResponse?>
                ) {
                    coroutineScope.launch {
                        response.body()?.hourly?.data?.let { addCurrentWeather(it) }
                        response.body()?.daily?.data?.let { forecastData ->
                            addListOfForecastData(forecastData)
                        }

                        updateLastFetch(getTodaysDate())

                    }
                }

                override fun onFailure(call: Call<WeatherResponse?>, t: Throwable) {
                    Log.v("Retrofit:error", t.message ?: "")
                    updateLastFetch(0)
                }

            }
        )
    }

    val mData: MutableLiveData<GeocodingData?> = MutableLiveData<GeocodingData?>()

    fun fetchGeocodingData(address: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val geocodingService = retrofit.create(GeocodingService::class.java)

        geocodingService.getGeocodingData(address, GEOCODING_API_KEY)
            ?.enqueue(object : Callback<GeocodingData?> {
                override fun onResponse(
                    @NonNull call: Call<GeocodingData?>,
                    @NonNull response: Response<GeocodingData?>
                ) {
                    if (!response.isSuccessful) {
                        return
                    }
                    mData.value = response.body()
                }

                override fun onFailure(@NonNull call: Call<GeocodingData?>, @NonNull t: Throwable) {
                    t.printStackTrace()
                    mData.value = null
                }
            })
    }

    fun getSettings(): LiveData<AppSettings?> = weatherDao.getSettings()

    fun updateSettings(settings: AppSettings) {
        coroutineScope.launch { weatherDao.addSettings(settings) }
    }

    fun updateTemperatureUnit(isCelsius: Boolean) {
        coroutineScope.launch { weatherDao.updateTemperatureUnit(isCelsius) }
    }

    private fun updateLastFetch(day: Int) {
        coroutineScope.launch { weatherDao.updateLastFetch(day) }
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepository? = null

        @JvmStatic
        fun getInstance(
            application: Application,
            coroutineScope: CoroutineScope
        ): WeatherRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildRepository(application, coroutineScope).also { INSTANCE = it }
            }

        private fun buildRepository(application: Application, coroutineScope: CoroutineScope) =
            WeatherRepository(application, coroutineScope)
    }

}