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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.said.minimalweather.R
import java.text.SimpleDateFormat
import java.util.*

class FooterAdapter(private val context: Context): RecyclerView.Adapter<FooterAdapter.ViewHolder>() {

    private var data = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.footer_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[0])
    }

    override fun getItemCount() = data.size

    fun setData(lastUpdate: List<Int>){
        data = lastUpdate
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(lastUpdated: Int) {
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            view.findViewById<TextView>(R.id.last_updated).text =
                context.getString(R.string.last_updated_string, simpleDateFormat.format(Date((lastUpdated * 1000).toLong() - TimeZone.getDefault().rawOffset)))
        }
    }
}