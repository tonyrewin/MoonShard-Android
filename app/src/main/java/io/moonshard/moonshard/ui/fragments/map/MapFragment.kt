package io.moonshard.moonshard.ui.fragments.map

import android.app.DatePickerDialog
import android.graphics.Bitmap
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
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.DateHolder
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_bottom_sheet_content.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet_category.*
import kotlinx.android.synthetic.main.bottom_sheet_info_content.*
import kotlinx.android.synthetic.main.bottom_sheet_time.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_map.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jxmpp.jid.impl.JidCreate
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MapFragment : MvpAppCompatFragment(), MapMainView, OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private var mMap: GoogleMap? = null

    @InjectPresenter
    lateinit var presenter: MapPresenter

    var sheetBehavior: BottomSheetBehavior<View>? = null
    var sheetInfoBehavior: BottomSheetBehavior<View>? = null

    private val defaultZoom: Float = 11F

    private var defaultMoscowLatitude: Double = 55.751244
    private var defaultMoscowLongitude: Double = 37.618423

    private var disposible: Disposable? = null

    val dateAndTime = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        (activity as? MainActivity)?.showBottomNavigationBar()
        (activity as? MainActivity)?.setMapActiveBottomBar()

        disposible = searchEventEt?.afterTextChangeEvents()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                if (!RoomsMap.isFilter) {
                    if (it.editable.toString().isEmpty()) {
                        clearSearch?.visibility = View.GONE
                    } else {
                        clearSearch?.visibility = View.VISIBLE
                    }

                    try {
                        val fragment =
                            childFragmentManager.findFragmentByTag("android:switcher:" + bottomSheetViewPager.id + ":" + 0)
                        (fragment as? ListChatsMapFragment)?.setFilter(
                            it.editable?.toString() ?: ""
                        )
                    } catch (e: Exception) {
                        Logger.d(e)
                    }
                }
            }


        //  MainApplication.getXmppConnection().addInRoster(JidCreate.entityBareFrom("dasdsladk@moonshard.tech"))

/*
        try {
            val search = UserSearchManager(MainApplication.getXmppConnection().connection)
            val j =
                JidCreate.domainBareFrom("search." + MainApplication.getXmppConnection().connection.xmppServiceDomain)
            val searchForm = search.getSearchForm(j)
            val answerForm = searchForm.createAnswerForm()
            answerForm.setAnswer("nick", "mykek")
            var data = search.getSearchResults(answerForm, j)

            val dsa = UserSearch()
            if (data.rows != null) {
                val it = data.rows as Iterator<ReportedData.Row>
                while (it.hasNext()) {
                    val row = it.next()
                    val iterator = row.getValues("jid") as Iterator<ReportedData.Row>
                    if (iterator.hasNext()) {
                        val value = iterator.next().toString()
                        com.orhanobut.logger.Logger.i("Iteartor values......", " $value")
                    }
                    //Log.i("Iteartor values......"," "+value);
                }
            }
            var kek = ""
        } catch (e: Exception) {
            var kek = ""
        }





        try {
            val search = UserSearchManager(MainApplication.getXmppConnection().connection)
            val j =
                JidCreate.domainBareFrom(search.searchServices.get(0))
            val searchForm = search.getSearchForm(j)
            val answerForm = searchForm.createAnswerForm()

            val userSearch = UserSearch()
            answerForm.setAnswer("nick", "mykek")
            // answerForm.setAnswer("nick", "qwe")
            var data = search.getSearchResults(answerForm, j)

               val results = userSearch.sendSearchForm(MainApplication.getXmppConnection().connection, answerForm, j)

            if (data.rows != null) {
                val it = data.rows as Iterator<ReportedData.Row>
                while (it.hasNext()) {
                    val row = it.next()
                    val iterator = row.getValues("jid") as Iterator<ReportedData.Row>
                    if (iterator.hasNext()) {
                        val value = iterator.next().toString()
                        com.orhanobut.logger.Logger.i("Iteartor values......", " $value")
                    }
                    //Log.i("Iteartor values......"," "+value);
                }
            }
            var kek = ""
        } catch (e: Exception) {
            var kek = ""
        }

*/

        // MainApplication.getXmppConnection().addUserToGroup2("myTestUser@moonshard.tech","myGroup")
        //  MainApplication.getXmppConnection().getGroup("myGroup")
        //ServiceDiscoveryManager.getInstanceFor(MainApplication.getXmppConnection().connection).discoverItems(JidCreate.from("conference.moonshard.tech"))

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

        calendarBtn?.setOnClickListener {
            //showDateBottomSheet()
            if (sheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED) {
                showDateBottomSheet()
            } else {
                hideDateBottomSheet()
            }
        }

        initDateBottomSheet()

        clearSearch?.setOnClickListener {
            if (RoomsMap.isFilter) {
                RoomsMap.clearFilters()
                presenter.getRooms("", "", "", null)
                hideDateBottomSheet()
                updateListRooms()
                clearSearch()
            } else {
                clearSearch()
            }
        }
    }

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

    override fun onMarkerClick(marker: Marker?): Boolean {
        defaultBottomSheet?.visibility = View.GONE
        infoBottomSheet?.visibility = View.VISIBLE
        for (i in RoomsMap.rooms.indices) {
            if (RoomsMap.rooms[i].latitude == marker?.position?.latitude) {
                RoomsMap.rooms[i].roomId?.let {

                    if (presenter.isJoin(it)) {
                        joinBtn?.visibility = View.GONE
                        joinBtn2?.visibility = View.GONE
                    } else {
                        joinBtn?.visibility = View.VISIBLE
                        joinBtn2?.visibility = View.VISIBLE
                    }

                    presenter.getCardInfo(RoomsMap.rooms[i])

                    locationInfoTv?.text =
                        getAddress(LatLng(RoomsMap.rooms[i].latitude, RoomsMap.rooms[i].longitude))

                    val date = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if (date.alreadyComeDate()) iconStartDate.visibility =
                        View.VISIBLE else iconStartDate.visibility = View.GONE

                    startDateEvent?.text =
                        "${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}"

                    joinBtn?.setSafeOnClickListener {
                        presenter.joinChat(RoomsMap.rooms[i].roomId!!)
                    }
                    readBtn?.setSafeOnClickListener {
                        presenter.readChat(RoomsMap.rooms[i].roomId!!)
                    }

                    joinBtn2?.setSafeOnClickListener {
                        presenter.joinChat(RoomsMap.rooms[i].roomId!!)
                    }
                    readBtn2?.setSafeOnClickListener {
                        presenter.readChat(RoomsMap.rooms[i].roomId!!)
                    }
                }
            }
        }
        return true
    }

    override fun showOnlineUserRoomInfo(onlineUser: String) {
        valueMembersInfoTv?.text = onlineUser
    }

    override fun showEventName(name: String) {
        groupNameInfoContentTv?.text = name
    }

    override fun showDistance(distance: String) {
        locationValueInfoTv?.text = distance
    }

    override fun showDescriptionEvent(description: String) {
        descriptionTv?.text = description
    }

    override fun hideJoinButtonsBottomSheet() {
        joinBtn?.visibility = View.GONE
        joinBtn2?.visibility = View.GONE
    }

    override fun showAvatar(avatar: Bitmap) {
        MainApplication.getMainUIThread().post {
            profileImageСard.setImageBitmap(avatar)
        }
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
            val latLng = LatLng(defaultMoscowLatitude, defaultMoscowLongitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom)
            mMap?.animateCamera(cameraUpdate)
        }
    }

    fun updateRooms(category: Category) {
        presenter.getRooms("", "", "", category)
    }

    fun updateRoomsLocale(events: ArrayList<RoomPin>) {
        showRoomsOnMap(events)
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

    private fun getAddress(location: LatLng): String {
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

    override fun showRoomsOnMap(rooms: ArrayList<RoomPin>) {
        mMap?.clear()
        for (i in rooms.indices) {
            when {
                rooms[i].category?.get(0)?.categoryName.toString() == "Тусовки" -> mMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                rooms[i].latitude,
                                rooms[i].longitude
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
                                rooms[i].latitude,
                                rooms[i].longitude
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
                                rooms[i].latitude,
                                rooms[i].longitude
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
                                rooms[i].latitude,
                                rooms[i].longitude
                            )
                        )
                        .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_culture)
                        )
                )
            }
        }
    }

    private fun setupBottomSheet() {
        val sectionsPagerAdapter = io.moonshard.moonshard.ui.adapters.PagerAdapter(
            childFragmentManager,
            context,
            io.moonshard.moonshard.ui.adapters.PagerAdapter.TabItem.LIST,
            io.moonshard.moonshard.ui.adapters.PagerAdapter.TabItem.CATEGORY
        )
        bottomSheetViewPager?.offscreenPageLimit = 1
        bottomSheetViewPager?.adapter = sectionsPagerAdapter
        bottomSheetTabs?.setupWithViewPager(bottomSheetViewPager)
        BottomSheetUtils.setupViewPager(bottomSheetViewPager)
    }

    private fun setupBottomSheetWithoutCategories() {
        val sectionsPagerAdapter = io.moonshard.moonshard.ui.adapters.PagerAdapter(
            childFragmentManager,
            context,
            io.moonshard.moonshard.ui.adapters.PagerAdapter.TabItem.LIST
        )
        bottomSheetViewPager?.offscreenPageLimit = 1
        bottomSheetViewPager?.adapter = sectionsPagerAdapter
        bottomSheetTabs?.setupWithViewPager(bottomSheetViewPager)
        BottomSheetUtils.setupViewPager(bottomSheetViewPager)
    }

    fun showCategoryBottomSheet() {
        removeCategoryFilterBtn?.setSafeOnClickListener {
            RoomsMap.clearFilters()
            presenter.getRooms("", "", "", null)
            hideCategoryBottomSheet()
            updateListRooms()
            clearCategoryAdapter()
        }

        bottomSheetCategory?.visibility = View.VISIBLE
        bottomSheetFind?.visibility = View.GONE
        categoryFilterName?.text = "Категория: " + RoomsMap.category?.categoryName
        sheetBehavior?.setPeekHeight(convertDpToPixel(100F, context), false)
    }

    fun hideCategoryBottomSheet() {
        bottomSheetCategory?.visibility = View.GONE
        bottomSheetFind?.visibility = View.VISIBLE
        sheetBehavior?.setPeekHeight(convertDpToPixel(120F, context), false)
    }

    fun showDateBottomSheet() {
        setupBottomSheetWithoutCategories()


        searchEventEt.isEnabled = false
        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetTime?.visibility = View.VISIBLE
        activityBottomSheetContent?.visibility = View.GONE
    }

    fun hideDateBottomSheet() {
        setupBottomSheet()
        searchEventEt.isEnabled = true
        sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetTime?.visibility = View.GONE
        activityBottomSheetContent?.visibility = View.VISIBLE
    }

    fun initDateBottomSheet() {
        todayTv?.setOnClickListener {
            presenter.setDateFilter(todayTv.text.toString())

            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetCategory?.visibility = View.VISIBLE
            activityBottomSheetContent?.visibility = View.VISIBLE
            bottomSheetTime?.visibility = View.GONE
            bottomSheetFind?.visibility = View.GONE
            categoryFilterName?.text = todayTv.text.toString()

            updateDateListRooms(todayTv.text.toString(),dateAndTime)

            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        tomorrowTv?.setOnClickListener {
            presenter.setDateFilter(tomorrowTv.text.toString())

            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetCategory?.visibility = View.VISIBLE
            activityBottomSheetContent?.visibility = View.VISIBLE
            bottomSheetTime?.visibility = View.GONE
            bottomSheetFind?.visibility = View.GONE
            categoryFilterName?.text = tomorrowTv.text.toString()

            updateDateListRooms(tomorrowTv.text.toString(),dateAndTime)

            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        weekendTv?.setOnClickListener {
            presenter.setDateFilter(weekendTv.text.toString())

            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetCategory?.visibility = View.VISIBLE
            activityBottomSheetContent?.visibility = View.VISIBLE
            bottomSheetTime?.visibility = View.GONE
            bottomSheetFind?.visibility = View.GONE
            categoryFilterName?.text = weekendTv.text.toString()

            updateDateListRooms(weekendTv.text.toString(),dateAndTime)

            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        chooseDateTv?.setOnClickListener {
            DatePickerDialog(
                activity!!, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        removeCategoryFilterBtn?.setOnClickListener {
            RoomsMap.clearFilters()
            presenter.getRooms("", "", "", null)
            hideCategoryBottomSheet()
            updateListRooms()
            clearSearch()
            setupBottomSheet()
        }
    }

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(Calendar.YEAR, year)
            dateAndTime.set(Calendar.MONTH, monthOfYear)
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDate(dateAndTime.get(Calendar.DAY_OF_MONTH), dateAndTime.get(Calendar.MONTH))

            presenter.setDateFilter("Выбрать дату", dateAndTime)

            bottomSheetCategory?.visibility = View.VISIBLE
            activityBottomSheetContent?.visibility = View.VISIBLE
            bottomSheetTime?.visibility = View.GONE
            bottomSheetFind?.visibility = View.GONE

            removeCategoryFilterBtn?.setOnClickListener {
                RoomsMap.clearFilters()
                presenter.getRooms("", "", "", null)
                hideCategoryBottomSheet()
                updateListRooms()
                clearSearch()
                setupBottomSheet()
            }
            updateDateListRooms("Выбрать дату", dateAndTime)

            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        }

    private fun setDate(dayOfMonth: Int, month: Int) {
        when (month) {
            0 -> {
                categoryFilterName.text = "$dayOfMonth января"
            }
            1 -> {
                categoryFilterName.text = "$dayOfMonth февраля"

            }
            2 -> {
                categoryFilterName.text = "$dayOfMonth марта"

            }
            3 -> {
                categoryFilterName.text = "$dayOfMonth апреля"

            }
            4 -> {
                categoryFilterName.text = "$dayOfMonth мая"

            }
            5 -> {
                categoryFilterName.text = "$dayOfMonth июня"

            }
            6 -> {
                categoryFilterName.text = "$dayOfMonth июля"

            }
            7 -> {
                categoryFilterName.text = "$dayOfMonth августа"

            }
            8 -> {
                categoryFilterName.text = "$dayOfMonth сенятбря"

            }
            9 -> {
                categoryFilterName.text = "$dayOfMonth октября"

            }
            10 -> {
                categoryFilterName.text = "$dayOfMonth ноября"
            }
            11 -> {
                categoryFilterName.text = "$dayOfMonth декабря"
            }
        }
    }

    fun clearCategoryAdapter() {
        val fragment =
            childFragmentManager.findFragmentByTag("android:switcher:" + bottomSheetViewPager.id + ":" + 1)
        (fragment as? CategoriesFragment)?.clearCategories()
    }

    fun clearSearch() {
        searchEventEt.text.clear()
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

    fun showMarkerBottomSheet(roomPin: RoomPin) {
        buttonsTop?.visibility = View.VISIBLE //for removing freeze

        sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        getLocationMarker(LatLng(roomPin.latitude, roomPin.longitude))

        defaultBottomSheet?.visibility = View.GONE
        infoBottomSheet?.visibility = View.VISIBLE

        if (presenter.isJoin(roomPin.roomId!!)) {
            joinBtn?.visibility = View.GONE
            joinBtn2?.visibility = View.GONE
        } else {
            joinBtn?.visibility = View.VISIBLE
            joinBtn2?.visibility = View.VISIBLE
        }

        presenter.getCardInfo(roomPin)

        locationInfoTv?.text =
            getAddress(LatLng(roomPin.latitude, roomPin.longitude))

        val date = DateHolder(roomPin.eventStartDate!!)
        if (date.alreadyComeDate()) iconStartDate.visibility =
            View.VISIBLE else iconStartDate.visibility = View.GONE

        startDateEvent?.text =
            "${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}"

        joinBtn?.setSafeOnClickListener {
            presenter.joinChat(roomPin.roomId!!)
        }
        readBtn?.setSafeOnClickListener {
            presenter.readChat(roomPin.roomId!!)
        }

        joinBtn2?.setSafeOnClickListener {
            presenter.joinChat(roomPin.roomId!!)
        }
        readBtn2?.setSafeOnClickListener {
            presenter.readChat(roomPin.roomId!!)
        }
    }

    private fun getLocationMarker(latLng: LatLng) {
        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(latLng, 12f)
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
        MainApplication.getMainUIThread().post {
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
        }
    }

    fun updateListRooms() {
        val fragment =
            childFragmentManager?.findFragmentByTag("android:switcher:" + bottomSheetViewPager.id + ":" + 0)
        (fragment as? ListChatsMapFragment)?.updateChats()
    }

    fun updateDateListRooms(date:String,calendar: Calendar?=null) {
        val fragment =
            childFragmentManager?.findFragmentByTag("android:switcher:" + bottomSheetViewPager.id + ":" + 0)
        (fragment as? ListChatsMapFragment)?.updateDateListRooms(date,calendar)
    }

    override fun onDestroyView() {
        try {
            super.onDestroyView()
            for (fragment in activity?.supportFragmentManager!!.fragments) {
                activity?.supportFragmentManager?.beginTransaction()?.remove(fragment)?.commit()
            }
        } catch (e: Exception) {
            Logger.d(e)
        }
    }
}
