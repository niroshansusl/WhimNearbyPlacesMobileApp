package com.niroshan.whimmobileappassignment.model.data

import com.niroshan.whimmobileappassignment.model.data.entities.NearByPlacesDetails
import com.google.gson.annotations.SerializedName

data class NearByPlacesDetailsResponse(
    @SerializedName("result") val result: NearByPlacesDetails
)