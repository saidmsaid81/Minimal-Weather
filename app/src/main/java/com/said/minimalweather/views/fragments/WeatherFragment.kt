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

package com.said.minimalweather.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.OrientationHelper
import com.said.minimalweather.R
import com.said.minimalweather.model.*
import com.said.minimalweather.databinding.WeatherFragmentBinding
import com.said.minimalweather.util.calculateTemperature
import com.said.minimalweather.util.getIconResId
import com.said.minimalweather.viewmodels.WeatherViewModel
import com.said.minimalweather.views.adapters.FooterAdapter
import com.said.minimalweather.views.adapters.ForecastWeatherAdapter

class WeatherFragment : Fragment(), RadioGroup.OnCheckedChangeListener {
    private lateinit var mBinding: WeatherFragmentBinding
    private lateinit var forecastWeatherAdapter: ForecastWeatherAdapter
    private lateinit var mViewModel: WeatherViewModel

    private lateinit var footerAdapter: FooterAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.weather_fragment, container, false)

        mViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
        context?.let {
            mBinding.toggle.setOnCheckedChangeListener(this)
            forecastWeatherAdapter = ForecastWeatherAdapter(it)
            footerAdapter = FooterAdapter(requireContext())
            val forecastListview = mBinding.forecastListview
            forecastListview.adapter = ConcatAdapter(forecastWeatherAdapter, footerAdapter)
            val dividerItemDecoration = DividerItemDecoration(it, OrientationHelper.VERTICAL)
            forecastListview.addItemDecoration(dividerItemDecoration)
        }

        mViewModel.getSettings().observe(viewLifecycleOwner) { settings: AppSettings? ->
            setBindingVariables(settings)
        }

        mBinding.addLocationButton.setOnClickListener {
            val dialogFragment = EnterLocationDialogFragment()
            fragmentManager?.let { dialogFragment.show(it, "dialog") }
        }

        setHasOptionsMenu(true)
        return mBinding.root
    }

    private fun setBindingVariables(settings: AppSettings?) {
        if (settings != null) {
            mViewModel.getCurrentWeather(settings.latitude, settings.longitude).observe(viewLifecycleOwner
            ) { currentWeatherData: CurrentWeatherData? ->
                if (currentWeatherData != null){
                    mBinding.currentWeather = currentWeatherData
                    mBinding.weatherIcon.setImageResource(getIconResId(currentWeatherData.icon))
                    mBinding.settings = settings
                    mBinding.toggle.clearCheck()
                    mBinding.toggle.check(if (!settings.isCelsius) R.id.fahreinheit else R.id.celcius)
                    footerAdapter.setData(listOf(currentWeatherData.time))

                    mBinding.loadingIndicator.visibility = GONE
                    mBinding.addLocationLayout.visibility = GONE
                    mBinding.locationDetails.visibility = VISIBLE
                    mBinding.weatherDetails.visibility = VISIBLE
                }
            }
            mViewModel.getForecastDataList().observe(viewLifecycleOwner
            ) { forecastWeatherData: List<ForecastWeatherData> ->
                forecastWeatherAdapter.setData(forecastWeatherData)
                mBinding.forecastListview.visibility = VISIBLE
            }
        } else {
            mBinding.loadingIndicator.visibility = GONE
            mBinding.locationDetails.visibility = GONE
            mBinding.weatherDetails.visibility = GONE
            mBinding.addLocationLayout.visibility = VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
                requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.celcius -> {
                mBinding.currentTemperature.text =
                        mBinding.currentWeather?.temperature
                                .toString()
                forecastWeatherAdapter.setUnit(true)

            }
            R.id.fahreinheit -> {
                mBinding.currentTemperature.text =
                        calculateTemperature(mBinding.currentWeather?.temperature ?: 0.0)
                                .toString()
                forecastWeatherAdapter.setUnit(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel.updateTemperatureUnits(mBinding.celcius.isChecked)
    }

}
