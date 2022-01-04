package com.niroshan.whimmobileappassignment.model.data.entities

import com.google.gson.annotations.SerializedName

data class NearByPlacesDetails(
    @SerializedName("name") val name: String?,
    @SerializedName("formatted_phone_number") val formattedPhoneNumber: String?,
    @SerializedName("formatted_address") val formattedAddress: String?,
    @SerializedName("website") val website: String?
)