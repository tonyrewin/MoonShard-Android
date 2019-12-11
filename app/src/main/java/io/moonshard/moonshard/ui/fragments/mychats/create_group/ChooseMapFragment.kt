package io.moonshard.moonshard.ui.fragments.mychats.create_group

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
import kotlinx.android.synthetic.main.fragment_choose_map.*
import java.io.IOException
import java.util.*

class ChooseMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveCanceledListener,
    GoogleMap.OnCameraIdleListener, GoogleMap.OnPoiClickListener {

    private var mMap: GoogleMap? = null

    private var latLngInterestPoint: PointOfInterest? = null

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = false
        map?.uiSettings?.isCompassEnabled = false

        mMap?.setOnCameraIdleListener(this)
        mMap?.setOnCameraMoveStartedListener(this)
        mMap?.setOnCameraMoveListener(this)
        mMap?.setOnCameraMoveCanceledListener(this)
        mMap?.setOnPoiClickListener(this)

        zoomPlus?.setOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }

        zoomMinus?.setOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }

        myLocationBtn?.setOnClickListener {
            getMyLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        doneBtn?.setOnClickListener {
            ChooseChatRepository.address = addressTv.text.toString()
            fragmentManager?.popBackStack()
        }

        back?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    override fun onPoiClick(pointOfInterest: PointOfInterest?) {
        latLngInterestPoint = pointOfInterest
        Toast.makeText(context, pointOfInterest?.name, Toast.LENGTH_SHORT).show()
        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(pointOfInterest?.latLng, mMap?.cameraPosition?.zoom!!)
        mMap?.animateCamera(cameraUpdate)
    }

    private fun getMyLocation() {
        if(MainApplication.getCurrentLocation()!=null){
            val latLng = LatLng(
                MainApplication.getCurrentLocation().latitude,
                MainApplication.getCurrentLocation().longitude
            )
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mMap?.cameraPosition?.zoom!!)
            mMap?.animateCamera(cameraUpdate)
        }
    }

    override fun onCameraMove() {
        var kek = ""
    }

    override fun onCameraMoveCanceled() {
        var kek = ""
    }

    //start camera
    override fun onCameraMoveStarted(p0: Int) {
        pin?.setImageResource(R.drawable.ic_pin_2_search)
    }

    //finish move camera
    override fun onCameraIdle() {
        pin?.setImageResource(R.drawable.ic_pin_2_stady)
        val latLng = mMap?.cameraPosition?.target

        latLng?.let {
            val address = getAddress(latLng)
            addressTv?.text = address
            ChooseChatRepository.lat = latLng.latitude.toFloat()
            ChooseChatRepository.lng = latLng.longitude.toFloat()
        }

        /*
    latLng?.let {
        latLngInterestPoint = null
        if(latLngInterestPoint?.latLng == latLng){
            var kek = ""
        }else{
            val adress = getAddress(latLng)
            Toast.makeText(context, adress, Toast.LENGTH_SHORT).show()
        }
    }

         */
    }

    fun getAddress(location: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.isNotEmpty()) {
                val address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                val postalCode = addresses[0].postalCode
                val knownName = addresses[0].featureName // Only if available else return NULL
                return address
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Информация отсутствует"
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}
