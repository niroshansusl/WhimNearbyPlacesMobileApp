package com.niroshan.whimmobileappassignment.model.repository


class RestaurantsRepository(private val repository: RestaurantsApiService) :
    RestaurantsApiService {

    override fun getRestaurantsDetails(
        placeId: String,
        fields: String,
        key: String
    ) = repository.getRestaurantsDetails(placeId, fields, key)

    override fun getNearByPlaces(
        location: String,
        radius: String,
        type: String,
        key: String
    ) = repository.getNearByPlaces(location, radius, type, key)

}