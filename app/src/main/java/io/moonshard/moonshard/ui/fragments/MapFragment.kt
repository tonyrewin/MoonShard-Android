package io.moonshard.moonshard.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.moonshard.moonshard.ui.BottomSheetFragment
import kotlinx.android.synthetic.main.fragment_map.*
import pub.devrel.easypermissions.EasyPermissions
import android.R
import android.widget.LinearLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import io.moonshard.moonshard.MainApplication
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*


class MapFragment : Fragment(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        map!!.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isCompassEnabled = false

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(io.moonshard.moonshard.R.layout.fragment_map, container, false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

       // val dialogView = layoutInflater.inflate(io.moonshard.moonshard.R.layout.bottom_sheet, null)
      //  val dialog = BottomSheetDialog(context!!)
      //  dialog.setContentView(dialogView)
      //  dialog.show()



        val bottomSheetBehavior = BottomSheetBehavior.from(myBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

        })

        //val kek = BottomSheetFragment()
       // kek.isCancelable = false
       // kek.show(activity!!.supportFragmentManager, "add_photo_dialog_fragment")
    }

    private fun getMyLocation() {
        val latLng = LatLng(MainApplication.getCurrentLocation().latitude, MainApplication.getCurrentLocation().longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mMap?.cameraPosition?.zoom!!)
        mMap?.animateCamera(cameraUpdate)
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
