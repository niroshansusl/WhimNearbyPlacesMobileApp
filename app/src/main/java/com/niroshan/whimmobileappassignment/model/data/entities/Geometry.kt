package com.niroshan.whimmobileappassignment.model.data.entities
import com.google.gson.annotations.SerializedName
import com.niroshan.whimmobileappassignment.model.data.entities.Location

data class Geometry(
    @SerializedName("location") val location: Location
)