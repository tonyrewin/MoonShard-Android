package io.moonshard.moonshard.ui.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import biz.laenger.android.vpbs.BottomSheetUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.SphericalUtil
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.presenter.MapPresenter
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.ui.activities.MainActivity
import kotlinx.android.synthetic.main.activity_bottom_sheet_content.*
import kotlinx.android.synthetic.main.bottom_sheet_info.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet_info.*
import kotlinx.android.synthetic.main.fragment_map.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import pub.devrel.easypermissions.EasyPermissions


class MapFragment : MvpAppCompatFragment(), MapMainView, OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private var mMap: GoogleMap? = null

    @InjectPresenter
    lateinit var presenter: MapPresenter

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = false
        map?.uiSettings?.isCompassEnabled = false
        mMap?.setOnMarkerClickListener(this)

        mMap?.setOnMapClickListener {
            defaultBottomSheet.visibility = View.VISIBLE
            infoBottomSheet.visibility = View.GONE
        }

        zoomPlus?.setOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }

        zoomMinus?.setOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }

        myLocationBtn?.setOnClickListener {
            getMyLocation()
        }

        presenter.getRooms("", "", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(io.moonshard.moonshard.R.layout.fragment_map, container, false)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        defaultBottomSheet?.visibility = View.GONE
        infoBottomSheet?.visibility = View.VISIBLE
        for (i in RoomsMap.rooms.indices) {
            if (RoomsMap.rooms[i].latitude.toDouble() == marker?.position?.latitude) {
                RoomsMap.rooms[i].roomId?.let {
                    var room = presenter.getRoom(it)
                    val distance = (calculationByDistance(
                        RoomsMap.rooms[i].latitude,
                        RoomsMap.rooms[i].longtitude
                    ))
                    locationValueTestTv?.text = distance
                }
                groupNameInfoTv?.text = RoomsMap.rooms[i].roomId?.split("@")?.get(0)
            }
        }
        return true
    }

    private fun calculationByDistance(latRoom: String, lngRoom: String): String {
        val myLat = MainApplication.getCurrentLocation().latitude
        val myLng = MainApplication.getCurrentLocation().longitude

        val km = SphericalUtil.computeDistanceBetween(
            LatLng(latRoom.toDouble(), lngRoom.toDouble()),
            LatLng(myLat, myLng)
        ).toInt() / 1000
        return if (km < 1) {
            (SphericalUtil.computeDistanceBetween(
                LatLng(
                    latRoom.toDouble(),
                    lngRoom.toDouble()
                ), LatLng(myLat, myLng)
            ).toInt()).toString() + " m away"
        }else{
            (SphericalUtil.computeDistanceBetween(
                LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                LatLng(myLat, myLng)
            ).toInt() / 1000).toString() + " km away"
        }
    }

    override fun showRoomsOnMap(rooms: ArrayList<RoomPin>) {
        for (i in rooms.indices) {
            if (rooms[i].category == "Культура") {
                mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longtitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(io.moonshard.moonshard.R.drawable.ic_marker_health)
                        )
                )
            } else {
                mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longtitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(io.moonshard.moonshard.R.drawable.ic_marker_health)
                        )
                )
            }
        }
    }

    override fun onDestroyView() {
        for (fragment in activity?.supportFragmentManager!!.fragments) {
            activity?.supportFragmentManager?.beginTransaction()?.remove(fragment)?.commit()
        }
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

    private fun setupClickBottomSheet() {
        val sectionsPagerAdapter = io.moonshard.moonshard.ui.adapters.PagerAdapter(
            activity!!.supportFragmentManager,
            context,
            io.moonshard.moonshard.ui.adapters.PagerAdapter.TabItem.INFO
        )
        bottomSheetAppbar.visibility = View.GONE

        bottomSheetViewPager.offscreenPageLimit = 1
        bottomSheetViewPager.adapter = sectionsPagerAdapter
        bottomSheetTabs.setupWithViewPager(bottomSheetViewPager)
        BottomSheetUtils.setupViewPager(bottomSheetViewPager)
    }

    fun setBottomJoinVisible() {
        infoBottomSheet.visibility = View.VISIBLE
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


        val myBottoms = view.findViewById<LinearLayout>(io.moonshard.moonshard.R.id.infoBottomSheet)
        val sheetBehavior = BottomSheetBehavior.from(myBottoms)


        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> bottomSheetInfo.visibility = View.GONE
                    BottomSheetBehavior.STATE_COLLAPSED -> bottomSheetInfo.visibility = View.VISIBLE
                }
            }

        })
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

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }
}
