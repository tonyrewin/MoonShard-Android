package io.moonshard.moonshard.ui.fragments.map

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.presenter.MapPresenter
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.fragments.chat.ChatFragment
import kotlinx.android.synthetic.main.activity_bottom_sheet_content.*
import kotlinx.android.synthetic.main.bottom_sheet_info.*
import kotlinx.android.synthetic.main.bottom_sheet_info_content.*
import kotlinx.android.synthetic.main.fragment_map.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.IOException
import java.util.*


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
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        defaultBottomSheet?.visibility = View.GONE
        infoBottomSheet?.visibility = View.VISIBLE
        for (i in RoomsMap.rooms.indices) {
            if (RoomsMap.rooms[i].latitude.toDouble() == marker?.position?.latitude) {
                RoomsMap.rooms[i].roomId?.let {
                    val roomInfo = presenter.getRoom(it)
                    val onlineUsers = presenter.getValueOnlineUsers(it)
                    roomInfo?.let {
                        val distance = (calculationByDistance(
                            RoomsMap.rooms[i].latitude,
                            RoomsMap.rooms[i].longtitude
                        ))

                        locationValueTestTv?.text = distance
                        groupNameInfoTv?.text = roomInfo.name
                        valueMembersTv?.text =
                            "${roomInfo.occupantsCount} человек, $onlineUsers онлайн"

                        joinGroupBtn?.setOnClickListener {
                            presenter.joinChat(RoomsMap.rooms[i].roomId!!)
                        }

                        readGroupBtn?.setOnClickListener {
                            presenter.joinChat(RoomsMap.rooms[i].roomId!!)
                        }

                        groupNameInfoContentTv?.text = roomInfo.name
                        valueMembersInfoTv?.text =
                            "${roomInfo.occupantsCount} человек, $onlineUsers онлайн"
                        locationValueInfoTv?.text = distance
                        locationInfoTv?.text = getAddress(
                            LatLng(
                                RoomsMap.rooms[i].latitude.toDouble(),
                                RoomsMap.rooms[i].longtitude.toDouble()
                            )
                        )
                        descriptionTv?.text = roomInfo.description

                        joinBtn?.setOnClickListener {
                            presenter.joinChat(RoomsMap.rooms[i].roomId!!)
                        }
                        readBtn?.setOnClickListener {
                            presenter.joinChat(RoomsMap.rooms[i].roomId!!)
                        }

                        joinBtn2?.setOnClickListener {
                            presenter.joinChat(RoomsMap.rooms[i].roomId!!)
                        }
                        readBtn2?.setOnClickListener {
                            presenter.joinChat(RoomsMap.rooms[i].roomId!!)
                        }
                    }
                }
            }
        }
        return true
    }

    override fun showChatScreens(chatId: String) {
        MainApplication.getMainUIThread().post {
            val bundle = Bundle()
            bundle.putString("chatId", chatId)
            val chatFragment = ChatFragment()
            chatFragment.arguments = bundle
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.add(R.id.container, chatFragment)?.hide(this)?.addToBackStack(null)
                ?.commit()
        }
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

    private fun calculationByDistance(latRoom: String, lngRoom: String): String {
        MainApplication.getCurrentLocation()?.let {
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
                ).toInt()).toString() + " метрах"
            } else {
                (SphericalUtil.computeDistanceBetween(
                    LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                    LatLng(myLat, myLng)
                ).toInt() / 1000).toString() + " км"
            }
        }
        return ""
    }

    override fun showRoomsOnMap(rooms: ArrayList<RoomPin>) {
        for (i in rooms.indices) {
            when {
                rooms[i].category == "Тусовки" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longtitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_star)
                        )
                )
                rooms[i].category == "Бизнес ивенты" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longtitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_business)
                        )
                )
                rooms[i].category == "Кружок по интересам" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longtitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_health)
                        )
                )
                rooms[i].category == "Культурные мероприятия" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longtitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_culture)
                        )
                )
            }
        }
    }

    override fun onDestroyView() {
        try {
            super.onDestroyView()
            for (fragment in activity?.supportFragmentManager!!.fragments) {
                activity?.supportFragmentManager?.beginTransaction()?.remove(fragment)?.commit()
            }
        } catch (e: Exception) {

        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        (activity as MainActivity).showBottomNavigationBar()
        setupBottomSheet()

        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val myBottoms = view.findViewById<LinearLayout>(R.id.infoBottomSheet)
        val sheetBehavior = BottomSheetBehavior.from(myBottoms)

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> buttonsTop.visibility = View.GONE
                    BottomSheetBehavior.STATE_COLLAPSED -> buttonsTop.visibility = View.VISIBLE
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
        presenter.onDestroy()
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
