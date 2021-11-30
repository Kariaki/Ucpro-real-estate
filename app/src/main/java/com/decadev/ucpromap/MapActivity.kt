package com.decadev.ucpromap

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.decadev.ucpromap.databinding.ActivityMapBinding
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.GeoJson
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import com.mapbox.maps.extension.style.style
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions

class MapActivity : AppCompatActivity(), PermissionsListener, OnMapReadyCallback,
    MapboxMap.OnMapClickListener {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var mapboxMap: MapboxMap? = null

    //to request permission to access user location
    private lateinit var permissionManager : PermissionsManager
    private lateinit var originLocation: Location // This is where we store current location.

    //it is the component that gives us the user location.
    private var locationComponent: LocationComponent? = null

    private val REQUEST_CODE_AUTOCOMPLETE = 17
    private val geoJsonSourceLayerId = "GeoJsonSourceLayerId"
    private val symbolIconId = "SymbolIconId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        binding = ActivityMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        sideBarIconsVisibility()
        pricesVisibilityLayout()
        regionArea()

    }

    private fun pricesVisibilityLayout() {
        binding.filterImg.setOnClickListener {
            if(binding.pricesLayout.visibility == View.GONE) {
                binding.pricesLayout.visibility = View.VISIBLE
            } else if(binding.pricesLayout.visibility == View.VISIBLE) {
                binding.pricesLayout.visibility = View.GONE
            }
        }
    }

    private fun regionArea() {
        binding.compassImg.setOnClickListener {
            if(binding.compassRegionLayout.visibility == View.GONE) {
                binding.compassRegionLayout.visibility = View.VISIBLE
            } else if(binding.compassRegionLayout.visibility == View.VISIBLE) {
                binding.compassRegionLayout.visibility = View.GONE
            }
        }
    }

    private fun sideBarIconsVisibility() {
        binding.homeImg.setOnClickListener {
            if (binding.mosqueImg.visibility == View.GONE && binding.houseImg.visibility == View.GONE) {
                binding.mosqueImg.visibility = View.VISIBLE
                binding.houseImg.visibility = View.VISIBLE
            } else if (binding.mosqueImg.visibility == View.VISIBLE && binding.houseImg.visibility == View.VISIBLE) {
                binding.mosqueImg.visibility = View.GONE
                binding.houseImg.visibility = View.GONE
            }
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day)) {
            style: Style? ->
            enableLocation(style)

            initSearchFab()

            setUpSource(style!!)

            setUpLayer(style)

            val locationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_location_on_24, null)
            val bitmapUtils = BitmapUtils.getBitmapFromDrawable(locationIcon)
            style.addImage(symbolIconId, bitmapUtils!!)
        }


    }

    private fun addDestinationIconSymbol(loadedMapStyle: Style?) {
        loadedMapStyle!!.addImage("destination-icon-id", BitmapFactory.decodeResource(this.resources, R.drawable.ic_baseline_add_location_24))

        val geoJsonSource = GeoJsonSource("destination-source-id")
        loadedMapStyle.addSource(geoJsonSource)
        val destinationSymbolLayer = SymbolLayer("destination-symbol-layer-id", "destination-source-id")
        destinationSymbolLayer.withProperties(PropertyFactory.iconImage("destination-icon-id"),
        PropertyFactory.iconAllowOverlap(true),
        PropertyFactory.iconIgnorePlacement(true))

        loadedMapStyle.addLayer(destinationSymbolLayer)
    }

    override fun onMapClick(point: LatLng): Boolean {
        val destinationPoint = Point.fromLngLat(point.longitude, point.latitude)
        val originPoint = Point.fromLngLat(locationComponent!!.lastKnownLocation!!.longitude, locationComponent!!.lastKnownLocation!!.latitude)

        val source = mapboxMap!!.style!!.getSourceAs<GeoJsonSource>("destination-icon-id")
        source?.setGeoJson(Feature.fromGeometry(destinationPoint))

        return true
    }

    private fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(SymbolLayer("SYMBOL_LAYER_ID", geoJsonSourceLayerId).withProperties(
            PropertyFactory.iconImage(symbolIconId),
            PropertyFactory.iconOffset(arrayOf(0f, -8f))
        ))
    }

    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(geoJsonSourceLayerId))
    }

    private fun initSearchFab() {
        binding.btnStart.setOnClickListener {v: View? ->
            val myIntent = PlaceAutocomplete.IntentBuilder()
                .accessToken(
                    (if (Mapbox.getAccessToken() != null) Mapbox.getAccessToken() else getString(R.string.access_token))!!
                ).placeOptions(PlaceOptions.builder()
                    .backgroundColor(Color.parseColor("#EEEEEE"))
                    .limit(10)
                    .build(PlaceOptions.MODE_CARDS))
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
                    source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson()))))


                    /** Move map Camera to the selected Location **/
                    mapboxMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                        .target(LatLng((selectedCarmenFeature.geometry() as Point?)!!.latitude(),
                            (selectedCarmenFeature.geometry() as Point?)!!.longitude()))
                        .zoom(14.0)
                        .build()), 4000)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation(loadedMapStyle: Style?) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            //do something
            locationComponent = mapboxMap!!.locationComponent
            locationComponent?.activateLocationComponent(this, loadedMapStyle!!)
            locationComponent?.setLocationComponentEnabled(true)


            //Set components camera mode
            locationComponent!!.setCameraMode(CameraMode.TRACKING)
        } else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    private fun setCameraPosition(location: Location) {
        mapboxMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(location.latitude, location.longitude), 13.0
        ))
    }


    override fun onExplanationNeeded(p0: MutableList<String>?) {
        Toast.makeText(this, "Your permission is needed to access your location on the map", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(p0: Boolean) {
        if(p0) {
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

}