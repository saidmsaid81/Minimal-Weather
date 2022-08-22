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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class GeocodingData(val results: List<Result>) {

    data class Result(
        @field:Expose @field:SerializedName("formatted_address") val formattedAddress: String,
        val geometry: Geometry
    )

}

data class Geometry(val location: Location)

data class Location(val lat: Float, val lng: Float)