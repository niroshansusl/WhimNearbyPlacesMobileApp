package com.niroshan.whimmobileappassignment.model.repository

import com.niroshan.whimmobileappassignment.model.data.NearByPlacesDetailsResponse
import com.niroshan.whimmobileappassignment.model.data.NearByPlacesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantsApiService {
    @GET("place/details/json")
    fun getRestaurantsDetails(@Query("place_id") placeId: String,
                              @Query("fields") fields:String,
                              @Query("key") key:String):Single<NearByPlacesDetailsResponse>
    
    @GET("place/nearbysearch/json")
    fun getNearByPlaces(@Query("location") location: String,
                        @Query("radius") radius: String,
                        @Query("type") type: String,
                        @Query("key") key: String):Single<NearByPlacesResponse>
    
}
