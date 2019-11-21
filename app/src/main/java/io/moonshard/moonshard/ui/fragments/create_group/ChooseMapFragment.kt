package io.moonshard.moonshard.ui.fragments.create_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import io.moonshard.moonshard.R
import pub.devrel.easypermissions.EasyPermissions

class ChooseMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {

    private var mMap: GoogleMap? = null


    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        map!!.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isCompassEnabled = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_map, container, false)
    }

    override fun onCameraMove() {
    }

    override fun onCameraMoveStarted(p0: Int) {
        //start camera
    }

    override fun onCameraIdle() {

        //finish moove camera

    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
