package io.moonshard.moonshard.ui.fragments.map

import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.*
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
import io.moonshard.moonshard.common.utils.Utils.convertDpToPixel
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.presenter.MapPresenter
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.fragments.map.bottomsheet.CategoriesFragment
import io.moonshard.moonshard.ui.fragments.map.bottomsheet.ListChatsMapFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.activity_bottom_sheet_content.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet_category.*
import kotlinx.android.synthetic.main.bottom_sheet_info_content.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_map.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MapFragment : MvpAppCompatFragment(), MapMainView, OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private var mMap: GoogleMap? = null

    @InjectPresenter
    lateinit var presenter: MapPresenter

    var sheetBehavior: BottomSheetBehavior<View>? = null
    var sheetInfoBehavior: BottomSheetBehavior<View>? = null

    private val defaultZoom: Float = 9F

    private var defaultMoscowlatitude: Double = 55.751244
    private var defaultMoscowlongitude: Double = 37.618423


    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = false
        map?.uiSettings?.isCompassEnabled = false
        mMap?.setOnMarkerClickListener(this)

        mMap?.setOnMapClickListener {
            defaultBottomSheet?.visibility = View.VISIBLE
            infoBottomSheet?.visibility = View.GONE
        }

        zoomPlus?.setSafeOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomIn())
        }

        zoomMinus?.setSafeOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomOut())
        }

        myLocationBtn?.setSafeOnClickListener {
            getMyLocation()
        }

        presenter.getRooms("", "", "", null)
        getZoomCenter()
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

                    if (presenter.isJoin(it)) {
                        joinBtn?.visibility = View.GONE
                        joinBtn2?.visibility = View.GONE
                    } else {
                        joinBtn?.visibility = View.VISIBLE
                        joinBtn2?.visibility = View.VISIBLE
                    }

                    val roomInfo = presenter.getRoom(it)
                    val onlineUsers = presenter.getValueOnlineUsers(it)
                    roomInfo?.let {
                        val distance = (calculationByDistance(
                            RoomsMap.rooms[i].latitude.toString(),
                            RoomsMap.rooms[i].longitude.toString()
                        ))

                        groupNameInfoContentTv?.text = roomInfo.name
                        valueMembersInfoTv?.text =
                            "${roomInfo.occupantsCount} человек, $onlineUsers онлайн"
                        locationValueInfoTv?.text = distance
                        locationInfoTv?.text = getAddress(
                            LatLng(
                                RoomsMap.rooms[i].latitude.toDouble(),
                                RoomsMap.rooms[i].longitude.toDouble()
                            )
                        )
                        descriptionTv?.text = roomInfo.description

                        joinBtn?.setSafeOnClickListener {
                            presenter.joinChat(RoomsMap.rooms[i].roomId!!, roomInfo.name)
                        }
                        readBtn?.setSafeOnClickListener {
                            presenter.readChat(RoomsMap.rooms[i].roomId!!, roomInfo.name)
                        }

                        joinBtn2?.setSafeOnClickListener {
                            presenter.joinChat(RoomsMap.rooms[i].roomId!!, roomInfo.name)
                        }
                        readBtn2?.setSafeOnClickListener {
                            presenter.readChat(RoomsMap.rooms[i].roomId!!, roomInfo.name)
                        }
                    }
                }
            }
        }
        return true
    }

    private fun getZoomCenter() {
        if (MainApplication.getCurrentLocation() != null) {
            val latLng = LatLng(
                MainApplication.getCurrentLocation().latitude,
                MainApplication.getCurrentLocation().longitude
            )
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom)
            mMap?.animateCamera(cameraUpdate)
        } else {
            val latLng = LatLng(defaultMoscowlatitude, defaultMoscowlongitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom)
            mMap?.animateCamera(cameraUpdate)
        }
    }

    fun update(category: Category) {
        presenter.getRooms("", "", "", category)
    }

    //hide
    fun collapsedBottomSheet() {
        sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun showBottomSheet() {
        defaultBottomSheet?.visibility = View.VISIBLE
    }

    override fun showChatScreens(chatId: String, stateChat: String) {
        MainApplication.getMainUIThread().post {
            val bundle = Bundle()
            bundle.putString("chatId", chatId)
            bundle.putBoolean("fromMap", true)
            bundle.putString("stateChat", stateChat)
            val mainChatFragment =
                MainChatFragment()
            mainChatFragment.arguments = bundle
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.add(R.id.container, mainChatFragment)?.hide(this)?.addToBackStack(null)
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
                return addresses[0].getAddressLine(0)
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
        mMap?.clear()
        for (i in rooms.indices) {
            when {
                rooms[i].category?.get(0)?.categoryName.toString() == "Тусовки" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_star)
                        )
                )
                rooms[i].category?.get(0)?.categoryName.toString() == "Бизнес ивенты" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_business)
                        )
                )
                rooms[i].category?.get(0)?.categoryName.toString() == "Кружок по интересам" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longitude.toDouble()
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_health)
                        )
                )
                rooms[i].category?.get(0)?.categoryName.toString() == "Культурные мероприятия" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude.toDouble(),
                                rooms[i].longitude.toDouble()
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


    //todo maybe change on childFragmentManager ?
    private fun setupBottomSheet() {
        val sectionsPagerAdapter = io.moonshard.moonshard.ui.adapters.PagerAdapter(
            activity!!.supportFragmentManager,
            context,
            io.moonshard.moonshard.ui.adapters.PagerAdapter.TabItem.LIST,
            io.moonshard.moonshard.ui.adapters.PagerAdapter.TabItem.CATEGORY
        )
        bottomSheetViewPager?.offscreenPageLimit = 1
        bottomSheetViewPager?.adapter = sectionsPagerAdapter
        bottomSheetTabs?.setupWithViewPager(bottomSheetViewPager)
        BottomSheetUtils.setupViewPager(bottomSheetViewPager)
    }

    fun showCategoryBottomSheet() {
        bottomSheetCategory?.visibility = View.VISIBLE
        bottomSheetFind?.visibility = View.GONE
        categoryFilterName?.text = "Категория: " + RoomsMap.category?.categoryName
        sheetBehavior?.setPeekHeight(convertDpToPixel(100F, context), false)
    }

    fun hideCategoryBottomSheet() {
        bottomSheetCategory?.visibility = View.GONE
        bottomSheetFind?.visibility = View.VISIBLE
        sheetBehavior?.setPeekHeight(convertDpToPixel(85F, context), false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        (activity as MainActivity).showBottomNavigationBar()
        (activity as? MainActivity)?.setMapActiveBottomBar()


/*

        val canvas = Canvas()
       canvas.drawRoundRect()

        fileIv.outlineProvider = object : ViewOutlineProvider() {

            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, 0, view!!.width, view!!.height, 16F)
                outline?.setRect(view!!.width/2, 0, view.width, view.height/2)
            }
        }


        fileIv.clipToOutline = true



 */

        // MainApplication.getXmppConnection().addUserToGroup2("myTestUser@moonshard.tech","myGroup")
        //  MainApplication.getXmppConnection().getGroup("myGroup")
        //ServiceDiscoveryManager.getInstanceFor(MainApplication.getXmppConnection().connection).discoverItems(JidCreate.from("conference.moonshard.tech"))


        var hashMap = hashMapOf<String, String>()
        hashMap.values.forEach {
            it
        }

        setupBottomSheet()

        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val llBottomSheet = view.findViewById<LinearLayout>(R.id.defaultBottomSheet)
        sheetBehavior = BottomSheetBehavior.from(llBottomSheet)

        val llInfoBottomSheet = view.findViewById<LinearLayout>(R.id.infoBottomSheet)
        sheetInfoBehavior = BottomSheetBehavior.from(llInfoBottomSheet)

        if (RoomsMap.isFilter) {
            showCategoryBottomSheet()
        } else {
            hideCategoryBottomSheet()
        }

        swipeInfoBtn?.setSafeOnClickListener {
            if (sheetInfoBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED) {
                sheetInfoBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                sheetInfoBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        swipeBtn?.setSafeOnClickListener {
            if (sheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED) {
                sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        removeFilterBtn?.setSafeOnClickListener {
            RoomsMap.clearFilters()
            presenter.getRooms("", "", "", null)
            hideCategoryBottomSheet()
            updateListRooms()
            clearCategoryAdapter()
        }

        sheetInfoBehavior?.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        buttonsTop?.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        buttonsTop?.visibility = View.VISIBLE
                    }
                }
            }
        })

        sheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        swipeBtn?.setImageResource(R.drawable.ic_line_bottom_sheet)
                        buttonsTop?.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        swipeBtn?.setImageResource(R.drawable.ic_bottom_sheet_up)
                        buttonsTop?.visibility = View.VISIBLE
                    }

                }
            }
        })
    }


    fun clearCategoryAdapter() {
        val fragment =
            fragmentManager?.findFragmentByTag("android:switcher:" + bottomSheetViewPager.id + ":" + 1)
        (fragment as? CategoriesFragment)?.clearCategories()
    }

    private fun getMyLocation() {
        if (MainApplication.getCurrentLocation() != null) {
            val latLng = LatLng(
                MainApplication.getCurrentLocation().latitude,
                MainApplication.getCurrentLocation().longitude
            )
            val cameraUpdate =
                CameraUpdateFactory.newLatLngZoom(latLng, 8f)
            mMap?.animateCamera(cameraUpdate)
        }
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
        MainApplication.getMainUIThread().post {
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
        }
    }


    fun updateListRooms() {
        val fragment =
            fragmentManager?.findFragmentByTag("android:switcher:" + bottomSheetViewPager.id + ":" + 0)
        (fragment as? ListChatsMapFragment)?.updateChats()
    }
}
