package com.niroshan.whimmobileappassignment.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.niroshan.whimmobileappassignment.R
import com.niroshan.whimmobileappassignment.databinding.ActivityRestaurantLayoutBinding
import com.niroshan.whimmobileappassignment.model.data.Resource
import com.niroshan.whimmobileappassignment.model.data.entities.Restaurant
import com.niroshan.whimmobileappassignment.utils.getLatLongString
import com.niroshan.whimmobileappassignment.viewmodel.NearByPlacesViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject


class NearByPlacesActivity : AppCompatActivity(), OnMapReadyCallback {
    private var locationPermissionGranted: Boolean? = null

    @Inject
    lateinit var nearByPlacesViewModel: NearByPlacesViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var lastKnownLocation: Location? = null
    private lateinit var binding: ActivityRestaurantLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        binding = ActivityRestaurantLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setUpViewModelObservers()
    }

    private fun setUpViewModelObservers() {
        nearByPlacesViewModel.restaurantsResponse.observe(this, Observer { response ->
            response?.results?.let {
                setUpPlaces(response.results)
            }
        })
        nearByPlacesViewModel.restaurantsDetailsResponse.observe(this, Observer {
            val bottomSheetModelDialogFragment = NearByPlacesDetailsDialogFragment()
            bottomSheetModelDialogFragment.nearByPlacesDetails = it?.result!!
            bottomSheetModelDialogFragment.show(
                supportFragmentManager,
                NearByPlacesDetailsDialogFragment::class.java.name
            )
        })
        nearByPlacesViewModel.status.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.loadingView?.progressViewLayout?.visibility = View.GONE
                }
                is Resource.Error -> {
                    binding.loadingView?.progressViewLayout?.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.loadingView?.progressViewLayout?.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setUpMapStyle()
        updateLocationUI()
        getDeviceLocation()

        mMap.setOnMarkerClickListener { marker ->
            val locationString = getLatLongString(marker.position?.latitude, marker.position?.longitude)
            val restaurant = nearByPlacesViewModel.restaurantsResponseMap.value?.get(locationString)
            restaurant?.let {
                nearByPlacesViewModel.getRestaurantDetails(it.place_id)
                mMap?.apply {
                    moveCamera(CameraUpdateFactory.newLatLng(LatLng(marker.position?.latitude,  marker.position?.longitude)))
                    animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM.toFloat()))
                }
            }
            true
        }
    }

    private fun setUpPlaces(restaurants: List<Restaurant>) {
        mMap.clear()
        if (restaurants.isEmpty()) return
        val builder = LatLngBounds.Builder()

        for (restaurant in restaurants) {
            val latLng = LatLng(restaurant.geometry.location.lat, restaurant.geometry.location.lng)
            createMarker(
                latLng,
                restaurant.name,
                restaurant.vicinity
            )?.let { builder.include(it.position) }
        }

        val bounds = builder.build();
        val width = resources.displayMetrics.widthPixels;
        val height = resources.displayMetrics.heightPixels;
        val padding = (width * 0.10).toInt()

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu)
    }

    private fun createMarker(latLng: LatLng, title: String, snippet: String): Marker? {
        return mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        )
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onStart() {
        super.onStart()
        getLocationPermission()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    getDeviceLocation()
                }
            }
        }
    }

    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted == true) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted == true) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )

                            val locationString = getLatLongString(
                                lastKnownLocation?.latitude,
                                lastKnownLocation?.longitude
                            )
                            nearByPlacesViewModel.getNearByPlaces(locationString)
                        }
                    } else {

                        mMap?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun setUpMapStyle() {
        try {
            val success: Boolean = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    companion object {
        private val TAG = NearByPlacesActivity::class.java.name
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100_1
    }
}