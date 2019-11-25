package io.moonshard.moonshard.ui.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import biz.laenger.android.vpbs.BottomSheetUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.presenter.MapPresenter
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.ui.activities.MainActivity
import kotlinx.android.synthetic.main.activity_bottom_sheet_content.*
import kotlinx.android.synthetic.main.fragment_map.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import pub.devrel.easypermissions.EasyPermissions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import io.moonshard.moonshard.R


class MapFragment : MvpAppCompatFragment(), MapMainView, OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    @InjectPresenter
    lateinit var presenter: MapPresenter

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

        presenter.getRooms("","","")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(io.moonshard.moonshard.R.layout.fragment_map, container, false)
    }

    override fun showRoomsOnMap(rooms: ArrayList<RoomPin>) {
        for(i in rooms.indices){
            if(rooms[i].category=="Культура"){
                mMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(rooms[i].latitude.toDouble(),rooms[i].longtitude.toDouble()))
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_health)
                        )
                )
            }else{
                mMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(rooms[i].latitude.toDouble(),rooms[i].longtitude.toDouble()))
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_health)
                        )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
    }

    private fun setupBottomSheet() {
        val sectionsPagerAdapter = io.moonshard.moonshard.ui.adapters.PagerAdapter(
            activity!!.supportFragmentManager,
            context,
            io.moonshard.moonshard.ui.adapters.PagerAdapter.TabItem.LIST,
            io.moonshard.moonshard.ui.adapters.PagerAdapter.TabItem.CATEGORY
        )
        bottomSheetViewPager.offscreenPageLimit = 1
        bottomSheetViewPager.adapter = sectionsPagerAdapter
        bottomSheetTabs.setupWithViewPager(bottomSheetViewPager)
        BottomSheetUtils.setupViewPager(bottomSheetViewPager)
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
        (activity as MainActivity).showBottomNavigationBar()
        setupBottomSheet()
    }

    private fun getMyLocation() {
        val latLng = LatLng(
            MainApplication.getCurrentLocation().latitude,
            MainApplication.getCurrentLocation().longitude
        )
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
