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

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.said.minimalweather.R
import com.said.minimalweather.model.AppSettings
import com.said.minimalweather.model.GeocodingData
import com.said.minimalweather.util.isDeviceOffline
import com.said.minimalweather.viewmodels.WeatherViewModel

class EnterLocationDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.add_location_layout, null)
        builder.setView(view)
                ?.setPositiveButton(R.string.save, null)
                ?.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                ?.setTitle(getString(R.string.location_hint))
        val dialog = builder.create()

        dialog.setOnShowListener {
            handleDialogButtonClicks(dialog, view, viewModel)
        }
        return dialog
    }

    private fun handleDialogButtonClicks(dialog: AlertDialog, view: View, viewModel: WeatherViewModel?) {
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            val location = view.findViewById<EditText>(R.id.location).text.toString()
            val messagesTextView = view.findViewById<TextView>(R.id.location_fetch_messages)
            messagesTextView.visibility = View.VISIBLE
            if (location.isBlank() || location.isEmpty()) {
                messagesTextView.text = getString(R.string.location_cannot_be_empty)
                return@setOnClickListener
            }
            if (!isDeviceOffline(context)) {
                messagesTextView.text = getString(R.string.updating)
                viewModel?.fetchGeocodingData(location)
                viewModel?.getGeocodingData()?.observe(this) { data: GeocodingData? ->
                    if (data != null && data.results.isNotEmpty()) {
                        val settings = AppSettings(
                            1,
                            data.results[0].geometry.location.lat,
                            data.results[0].geometry.location.lng,
                            data.results[0].formattedAddress,
                            0, true
                        )
                        viewModel.updateSettings(settings)
                        dialog.dismiss()
                    } else if (data != null && data.results.isEmpty())
                        messagesTextView.text = getString(R.string.unavailable_location)
                    else
                        messagesTextView.text = getString(R.string.error_updating_location)
                }
            } else {
                messagesTextView.text = getString(R.string.error_updating_location)
            }

        }
    }
}
//<div>Icons made by <a href="https://www.flaticon.com/authors/good-ware" title="Good Ware">Good Ware</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>