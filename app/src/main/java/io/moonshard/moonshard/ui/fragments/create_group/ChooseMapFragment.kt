package io.moonshard.moonshard.ui.fragments.create_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import io.moonshard.moonshard.R
import kotlinx.android.synthetic.main.fragment_choose_map.*
import kotlinx.android.synthetic.main.fragment_choose_map.mapView
import kotlinx.android.synthetic.main.fragment_map.*
import pub.devrel.easypermissions.EasyPermissions

class ChooseMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraIdleListener {


    private var mMap: GoogleMap? = null

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = false
        map?.uiSettings?.isCompassEnabled = false

        mMap?.setOnCameraIdleListener(this)
        mMap?.setOnCameraMoveStartedListener(this)
        mMap?.setOnCameraMoveListener(this)
        mMap?.setOnCameraMoveCanceledListener(this)
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

    //finish moove camera
    override fun onCameraIdle() {
        pin?.setImageResource(R.drawable.ic_pin_2_stady)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
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
