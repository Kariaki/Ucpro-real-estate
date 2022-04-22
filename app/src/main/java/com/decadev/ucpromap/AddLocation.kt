package com.decadev.ucpromap

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.decadev.ucpromap.databinding.ActivityAddLocationBinding
import com.decadev.ucpromap.databinding.ActivityMapBinding
import com.decadev.ucpromap.repository.Repository
import com.decadev.ucpromap.utils.MainViewModelFactory
import com.decadev.ucpromap.viewModel.MainViewModel
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import java.util.*
import kotlin.collections.ArrayList

class AddLocation : AppCompatActivity(), PermissionsListener, OnMapReadyCallback
//    , MapboxMap.OnMapClickListener
{
    private lateinit var binding: ActivityAddLocationBinding
    private lateinit var mapView: MapView
    private var mapboxMap: MapboxMap? = null
    private var destinationMarker: Marker? = null
    private lateinit var originPosition: Point
    private lateinit var destinationPosition: Point

    /**to request permission to access user location **/
    private lateinit var permissionManager: PermissionsManager
    private var originLocation: Location? = null // This is where we store current location.

    /** it is the component that gives us the user location. **/
    private var locationComponent: LocationComponent? = null
    private var locationEngine: LocationEngine? = null

    private val REQUEST_CODE_AUTOCOMPLETE = 17
    private val geoJsonSourceLayerId = "GeoJsonSourceLayerId"
    private val symbolIconId = "SymbolIconId"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        Mapbox.getInstance(this, getString(R.string.access_token))
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        val pox_details_circle = findViewById<View>(R.id.pox_details_circle)
        val pox_details_text = findViewById<TextView>(R.id.pox_details_text)

        //        for animation code
        Handler(Looper.getMainLooper()).postDelayed({

            pox_details_circle.background.setTint(Color.rgb(253,216,53))
            pox_details_text.setTextColor(Color.rgb(253,216,53))

        }, 1500)
    }



    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day)) { style: Style? ->
            enableLocation(style)
            //   addDestinationIconSymbol(style)
//            mapboxMap.addOnMapClickListener(this)

            initSearchFab()

            setUpSource(style!!)

            setUpLayer(style)


//            this code for fined search place
            val locationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_location_on_24
                    , null)
            val bitmapUtils = BitmapUtils.getBitmapFromDrawable(locationIcon)
            style.addImage(symbolIconId, bitmapUtils!!)
        }

    }


    private fun getLocation(x0: Double, y0: Double, radius: Int): LatLng {
        val random = Random()

        val radiusInDegrees = radius / 111000f

        val u = random.nextDouble()
        val v = random.nextDouble()
        val w = radiusInDegrees * Math.sqrt(u)
        val t = 2 * Math.PI * v
        val x = w * Math.cos(t)
        val y = w * Math.sin(t)

        val new_x = x / Math.cos(Math.toRadians(y0))

        val newLocation = LatLng()

        newLocation.longitude = new_x + x0
        newLocation.latitude = new_x + y0

        return newLocation
    }

    private fun offSet(size: Int, init: Float): Array<Float> {
        return offSet(size, init)
    }

    private fun addDestinationIconSymbol(loadedMapStyle: Style?) {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val locationDrawable = getDrawable(R.drawable.ic_house_icon)

        val bitmap = Bitmap.createBitmap(
            locationDrawable!!.intrinsicWidth,
            locationDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        loadedMapStyle!!.addImage("destination-icon-id", bitmap)

        val geoJsonSource = GeoJsonSource("destination-source-id")
        loadedMapStyle.addSource(geoJsonSource)
        val destinationSymbolLayer =
            SymbolLayer("destination-symbol-layer-id", "destination-source-id")
        destinationSymbolLayer.withProperties(
            PropertyFactory.iconImage("destination-icon-id"),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
        )

        loadedMapStyle.addLayer(destinationSymbolLayer)
    }

//    override fun onMapClick(point: LatLng): Boolean {
//        destinationMarker = mapboxMap?.addMarker(MarkerOptions().apply {
//            position(point)
//        })
//
//        destinationPosition = Point.fromLngLat(point.longitude, point.latitude)
//        originPosition = Point.fromLngLat(
//            locationComponent?.lastKnownLocation!!.longitude,
//            locationComponent?.lastKnownLocation!!.latitude
//        )
//
//        return true
//    }

    private fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(
            SymbolLayer("SYMBOL_LAYER_ID", geoJsonSourceLayerId).withProperties(
                PropertyFactory.iconImage(symbolIconId),
                PropertyFactory.iconOffset(arrayOf(0f, -8f))
            )
        )
    }

    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(geoJsonSourceLayerId))
    }

    private fun initSearchFab() {
        binding.btnStart.setOnClickListener { v: View? ->
            val myIntent = PlaceAutocomplete.IntentBuilder()
                .accessToken(
                    (if (Mapbox.getAccessToken() != null) Mapbox.getAccessToken() else getString(R.string.access_token))!!
                ).placeOptions(
                    PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS)
                )
                .build(this)
            startActivityIfNeeded(myIntent, REQUEST_CODE_AUTOCOMPLETE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            /** Retrieve selected location's carmenFeature **/
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)

            /** Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
             * Then retrieve and update the source designated for showing a selected location's symbol layer icon
             */
            if (mapboxMap != null) {
                val style = mapboxMap!!.style
                if (style != null) {
                    val source = style.getSourceAs<GeoJsonSource>(geoJsonSourceLayerId)
                    source?.setGeoJson(
                        FeatureCollection.fromFeatures(
                            arrayOf(
                                Feature.fromJson(
                                    selectedCarmenFeature.toJson()
                                )
                            )
                        )
                    )


                    /** Move map Camera to the selected Location **/
                    mapboxMap!!.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(
                                    LatLng(
                                        (selectedCarmenFeature.geometry() as Point?)!!.latitude(),
                                        (selectedCarmenFeature.geometry() as Point?)!!.longitude()
                                    )
                                )
                                .zoom(14.0)
                                .build()
                        ), 4000
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation(loadedMapStyle: Style?) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            /**do something**/
            locationComponent = mapboxMap!!.locationComponent
            locationComponent?.activateLocationComponent(this, loadedMapStyle!!)
            locationComponent?.setLocationComponentEnabled(true)


            /**Set components camera mode**/
            locationComponent!!.setCameraMode(CameraMode.TRACKING)
        } else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    private fun setCameraPosition(location: Location) {
        mapboxMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 13.0
            )
        )
    }


    override fun onExplanationNeeded(p0: MutableList<String>?) {
        Toast.makeText(
            this,
            "Your permission is needed to access your location on the map",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(p0: Boolean) {
        if (p0) {
            enableLocation(mapboxMap!!.style)
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    fun goToBack(view: View) {
        this.finish()
    }

    fun goingToNext(view: View) {
        startActivity(Intent(this, AddPox::class.java))
    }


}