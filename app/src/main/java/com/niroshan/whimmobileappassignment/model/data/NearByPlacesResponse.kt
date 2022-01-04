package com.niroshan.whimmobileappassignment.model.data

import com.niroshan.whimmobileappassignment.model.data.entities.Restaurant
import com.google.gson.annotations.SerializedName

data class NearByPlacesResponse(
    @SerializedName("results") val results: List<Restaurant>
)