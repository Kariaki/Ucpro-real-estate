package com.decadev.ucpromap

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.decadev.ucpromap.databinding.ActivityMapBinding
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import java.util.*
import kotlin.collections.ArrayList

class MapActivity : AppCompatActivity(), PermissionsListener, OnMapReadyCallback,
    MapboxMap.OnMapClickListener {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var mapboxMap: MapboxMap? = null
    private var destinationMarker: Marker? = null
    private lateinit var originPosition: Point
    private lateinit var destinationPosition: Point

    //to request permission to access user location
    private lateinit var permissionManager : PermissionsManager
    private var originLocation: Location? = null // This is where we store current location.

    //it is the component that gives us the user location.
    private var locationComponent: LocationComponent? = null
    private var locationEngine: LocationEngine? = null

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


//        sideBarIconsVisibility()
//        pricesVisibilityLayout()
//        regionArea()
        userSideBar()
        addPoxButton()
        rentVisibility()
        costVisibility()
        depositVisibility()

        binding.addPoxTextView.setOnClickListener {
            val dialog = PropertyTypeDialogFragment()

            dialog.show(supportFragmentManager, "customDialog")
        }

    }

    private fun rentVisibility() {
        binding.rentTextView.setOnClickListener {
            if (binding.rentAndCostPricesLayout.visibility == View.GONE) {
                binding.depositPricesLayout.visibility = View.GONE
                binding.rentAndCostPricesLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun costVisibility() {
        binding.costTextView.setOnClickListener {
            if (binding.rentAndCostPricesLayout.visibility == View.GONE) {
                binding.depositPricesLayout.visibility = View.GONE
                binding.rentAndCostPricesLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun depositVisibility() {
        binding.depositTextView.setOnClickListener {
            if (binding.depositPricesLayout.visibility == View.GONE) {
                binding.rentAndCostPricesLayout.visibility = View.GONE
                binding.depositPricesLayout.visibility = View.VISIBLE
            }
        }
    }

//    private fun pricesVisibilityLayout() {
//        binding.filterImg.setOnClickListener {
//            if(binding.pricesLayout.visibility == View.GONE) {
//                binding.pricesLayout.visibility = View.VISIBLE
//            } else if(binding.pricesLayout.visibility == View.VISIBLE) {
//                binding.pricesLayout.visibility = View.GONE
//            }
//        }
//    }

//    private fun regionArea() {
//        binding.compassImg.setOnClickListener {
//            if(binding.compassRegionLayout.visibility == View.GONE) {
//                binding.compassRegionLayout.visibility = View.VISIBLE
//            } else if(binding.compassRegionLayout.visibility == View.VISIBLE) {
//                binding.compassRegionLayout.visibility = View.GONE
//            }
//        }
//    }

//    private fun sideBarIconsVisibility() {
//        binding.homeImg.setOnClickListener {
//            if (binding.mosqueImg.visibility == View.GONE && binding.houseImg.visibility == View.GONE) {
//                binding.mosqueImg.visibility = View.VISIBLE
//                binding.houseImg.visibility = View.VISIBLE
//            } else if (binding.mosqueImg.visibility == View.VISIBLE && binding.houseImg.visibility == View.VISIBLE) {
//                binding.mosqueImg.visibility = View.GONE
//                binding.houseImg.visibility = View.GONE
//            }
//        }
//    }

    private fun userSideBar() {
        binding.personImg.setOnClickListener {
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)

        }

//        binding.personImg.setOnClickListener {
//            if (binding.settingsImg.visibility == View.GONE && binding.addImg.visibility == View.GONE && binding.favoriteImg.visibility == View.GONE) {
//                binding.settingsImg.visibility = View.VISIBLE
//                binding.addImg.visibility = View.VISIBLE
//                binding.favoriteImg.visibility = View.VISIBLE
//            } else if (binding.settingsImg.visibility == View.VISIBLE && binding.addImg.visibility == View.VISIBLE && binding.favoriteImg.visibility == View.VISIBLE || binding.addPoxTextView.visibility == View.VISIBLE) {
//                binding.settingsImg.visibility = View.GONE
//                binding.addImg.visibility = View.GONE
//                binding.favoriteImg.visibility = View.GONE
//                binding.addPoxTextView.visibility = View.GONE
//
//                val myIntent = Intent(this, MainActivity::class.java)
//                startActivity(myIntent)
//            }
//        }
    }

    private fun addPoxButton() {
            binding.addImg.setOnClickListener {
                if (binding.addPoxTextView.visibility == View.GONE) {
                    binding.addPoxTextView.visibility = View.VISIBLE
                } else if (binding.addPoxTextView.visibility == View.VISIBLE) {
                    binding.addPoxTextView.visibility = View.GONE
                }
            }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day)) {
            style: Style? ->
            enableLocation(style)
         //   addDestinationIconSymbol(style)
            mapboxMap.addOnMapClickListener(this)

            initSearchFab()

            setUpSource(style!!)

            setUpLayer(style)

            val locationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_other_houses_24, null)
            val bitmapUtils = BitmapUtils.getBitmapFromDrawable(locationIcon)
            style.addImage(symbolIconId, bitmapUtils!!)
        }


    }

    private fun multipleMarkers() {
        val options = ArrayList<SymbolOptions>()
        for (i in 0..4) {
            options.add(SymbolOptions()
                .withLatLng(getLocation(1.0, 2.0, 50))
                .withIconImage("house_image")
                .withIconSize(1.5f)
                .withIconOffset(offSet(0, -1.5f))
                .withTextField("test marker")
                .withTextHaloColor("rgba(255, 255, 255, 100)")
                .withTextHaloWidth(5.0f)
                .withTextAnchor("top")
                .withTextOffset(offSet(0, -1.5f))
                .withDraggable(false))
        }

    }

    private fun getLocation(x0: Double, y0: Double, radius: Int) : LatLng {
        val random = Random()

        val radiusInDegrees = radius/111000f

        val u = random.nextDouble()
        val v = random.nextDouble()
        val w = radiusInDegrees * Math.sqrt(u)
        val t = 2 * Math.PI * v
        val x = w * Math.cos(t)
        val y = w * Math.sin(t)

        val new_x = x/Math.cos(Math.toRadians(y0))

        val newLocation = LatLng()

        newLocation.longitude = new_x + x0
        newLocation.latitude = new_x + y0

        return newLocation
    }

    private fun offSet(size: Int, init: Float): Array<Float> {
        return offSet(size, init)
    }

    private fun addDestinationIconSymbol(loadedMapStyle: Style?) {
//        val options = BitmapFactory.Options()
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val locationDrawable = getDrawable(R.drawable.ic_baseline_other_houses_24)

        val bitmap = Bitmap.createBitmap(locationDrawable!!.intrinsicWidth, locationDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        loadedMapStyle!!.addImage("destination-icon-id", bitmap)

        val geoJsonSource = GeoJsonSource("destination-source-id")
        loadedMapStyle.addSource(geoJsonSource)
        val destinationSymbolLayer = SymbolLayer("destination-symbol-layer-id", "destination-source-id")
        destinationSymbolLayer.withProperties(PropertyFactory.iconImage("destination-icon-id"),
        PropertyFactory.iconAllowOverlap(true),
        PropertyFactory.iconIgnorePlacement(true))

        loadedMapStyle.addLayer(destinationSymbolLayer)
    }

    override fun onMapClick(point: LatLng): Boolean {
        destinationMarker = mapboxMap?.addMarker(MarkerOptions().apply {
            position(point)
        })

        destinationPosition = Point.fromLngLat(point.longitude, point.latitude)
        originPosition = Point.fromLngLat(locationComponent?.lastKnownLocation!!.longitude, locationComponent?.lastKnownLocation!!.latitude)

//        val destinationMarker = mapboxMap.addMark
//        val destinationPoint = Point.fromLngLat(point.longitude, point.latitude)
//        val originPoint = Point.fromLngLat(locationComponent!!.lastKnownLocation!!.longitude, locationComponent!!.lastKnownLocation!!.latitude)

//        val source = mapboxMap!!.style!!.getSourceAs<GeoJsonSource>("destination-icon-id")
//        source?.setGeoJson(Feature.fromGeometry(destinationPoint))

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