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

package com.said.minimalweather.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.said.minimalweather.R
import com.said.minimalweather.model.ForecastWeatherData
import com.said.minimalweather.databinding.ForecastBinding
import com.said.minimalweather.util.calculateTemperature
import com.said.minimalweather.util.getIconResId

class ForecastWeatherAdapter(private val context: Context) :
    RecyclerView.Adapter<ForecastWeatherAdapter.ViewHolder>() {

    private var isCelsius: Boolean = false
    private var list: List<ForecastWeatherData?> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ForecastBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.forecast,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].let {
            holder.binding.forecastWeather = it
            if (it != null) {
                holder.binding.tomorrowWeatherIcon.setImageResource(getIconResId(it.icon))
            }
            if (!isCelsius) {
                holder.binding.tomorrowMaxTemperature.text = context.getString(
                    R.string.max_temp, calculateTemperature(
                        it?.maximumTemperature
                            ?: 0.0
                    )
                )
                holder.binding.tomorrowMinTemperature.text = context.getString(
                    R.string.min_temp, calculateTemperature(
                        it?.minimumTemperature
                            ?: 0.0
                    )
                )
            } else {
                holder.binding.tomorrowMaxTemperature.text =
                    context.getString(R.string.max_temp, it?.maximumTemperature)
                holder.binding.tomorrowMinTemperature.text =
                    context.getString(R.string.min_temp, it?.minimumTemperature)
            }
        }

    }

    fun setData(list: List<ForecastWeatherData?>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setUnit(isCelsius: Boolean) {
        this.isCelsius = isCelsius
        notifyDataSetChanged()
    }


    class ViewHolder(val binding: ForecastBinding) : RecyclerView.ViewHolder(binding.root)


}