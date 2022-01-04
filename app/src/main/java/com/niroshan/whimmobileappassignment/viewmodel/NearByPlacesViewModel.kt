package com.niroshan.whimmobileappassignment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niroshan.whimmobileappassignment.model.data.Resource
import com.niroshan.whimmobileappassignment.model.data.NearByPlacesDetailsResponse
import com.niroshan.whimmobileappassignment.model.data.NearByPlacesResponse
import com.niroshan.whimmobileappassignment.model.data.entities.Restaurant
import com.niroshan.whimmobileappassignment.model.repository.RestaurantsRepository
import com.niroshan.whimmobileappassignment.utils.getLatLongString
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class NearByPlacesViewModel @Inject constructor(
    private val restaurantsRepository: RestaurantsRepository,
    @Named("secret_key") val key: String) : ViewModel() {
    var restaurantsResponseMap = MutableLiveData<Map<String, Restaurant>>()
    val restaurantsResponse = MutableLiveData<NearByPlacesResponse>()
    val restaurantsDetailsResponse = MutableLiveData<NearByPlacesDetailsResponse>()
    val status = MutableLiveData<Resource<Boolean>>()
    private val compositeDisposable = CompositeDisposable()

    fun getNearByPlaces(location: String) {
        status.value = Resource.Loading(null)
        compositeDisposable?.add(restaurantsRepository.getNearByPlaces(
            location,
            "5000",
            "restaurant",
            key
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                status.value = Resource.Success(true)
                restaurantsResponse.value = it
                restaurantsResponseMap.value = convert(it.results)
            }, {
                status.value = Resource.Error("Error")
                restaurantsResponse.value = null
                restaurantsResponseMap.value = emptyMap()
            })
        )
    }

    fun getRestaurantDetails(id: String) {
        status.value = Resource.Loading(null)
        compositeDisposable.add(
            restaurantsRepository.getRestaurantsDetails(
                id,
                "name,formatted_phone_number,formatted_address,website",
                key
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    status.value = Resource.Success(true)
                    restaurantsDetailsResponse.value = it
                }, {
                    status.value = Resource.Error("Error")
                    restaurantsDetailsResponse.value = null
                })
        )
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun convert(restaurants: List<Restaurant>): Map<String, Restaurant> {
        val map = HashMap<String, Restaurant>()
        for (restaurant in restaurants) {
            map[getLatLongString(
                restaurant.geometry.location.lat,
                restaurant.geometry.location.lng
            )] = restaurant
        }
        return map
    }
}