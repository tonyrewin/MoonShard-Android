package io.moonshard.moonshard.ui.fragments.mychats.chat.info.event

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import io.moonshard.moonshard.R
import io.moonshard.moonshard.StreamUtil
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.presentation.presenter.chat.info.ManageEventPresenter
import io.moonshard.moonshard.presentation.view.chat.info.ManageEventView
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import io.moonshard.moonshard.common.utils.DateHolder
import kotlinx.android.synthetic.main.fragment_manage_event.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.URLConnection
import java.util.*


class ManageEventFragment : MvpAppCompatFragment(), ManageEventView {

    @InjectPresenter
    lateinit var presenter: ManageEventPresenter

    var idChat = ""
    var bytes: ByteArray? = null
    var mimeType: String? = null
    private val dateAndTime = Calendar.getInstance()

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(Calendar.YEAR, year)
            dateAndTime.set(Calendar.MONTH, monthOfYear)
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setStartDate(dayOfMonth, monthOfYear)
            ChangeEventRepository.setStartDate(dateAndTime)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
            presenter.getInfoChat(idChat)
        }

        locationLayout?.setSafeOnClickListener {
            ChangeEventRepository.name = nameEt?.text.toString()
            ChangeEventRepository.description = descriptionEt?.text.toString()
            methodRequiresTwoPermission()
        }

        membersLayout?.setSafeOnClickListener {
            showMembersScreen()
        }

        adminsLayout?.setSafeOnClickListener {
            showAdminsScreen()
        }

        readBtn?.setSafeOnClickListener {
            presenter.setData(
                nameEt.text.toString(),
                descriptionEt.text.toString(),
                idChat,
                bytes,
                mimeType,
                ChangeEventRepository.event!!
            )
        }

        backBtn?.setSafeOnClickListener {
            ChangeEventRepository.clean()
            fragmentManager?.popBackStack()
        }

        avatarIv?.setSafeOnClickListener {
            chooseFile()
        }

        dateStartEvent?.setSafeOnClickListener {
            DatePickerDialog(
                activity!!, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        timesLayout?.setSafeOnClickListener {
            ChangeEventRepository.name = nameEt?.text.toString()
            ChangeEventRepository.description = descriptionEt?.text.toString()
            showTimesScreen()
        }

        destroyRoom?.setSafeOnClickListener {
            presenter.destroyRoom(idChat)
        }

        manageTicketsBtn?.setSafeOnClickListener {
            //todo
        }
    }

    override fun showTimeDays(timeDays: Long) {
        timeTv?.text = getTtlToDays(timeDays)
    }

    override fun setStartDate(dayOfMonth: Int, month: Int) {
        val date = DateHolder(System.currentTimeMillis())
        dateTv.text = "$dayOfMonth " + date.getMonthString(month)
    }

    fun getTtlToDays(ttl:Long):String{
        var time =""
        when(ttl){
            (60*60*24).toLong() -> time = "1 " + getString(R.string.day) + ""
            (60*60*48).toLong() -> time = "2 " + getString(R.string.days234) + ""
            (60*60*(24*3)).toLong() -> time = "3 " + getString(R.string.days234) + ""
            (60*60*(24*4)).toLong() -> time = "4 " + getString(R.string.days234) + ""
            (60*60*(24*5)).toLong() -> time = "5 " + getString(R.string.days) + ""
            (60*60*(24*6)).toLong() -> time = "6 " + getString(R.string.days) + ""
            (60*60*(24*7)).toLong() -> time = "" + getString(R.string.a_week) + ""
        }
        return time
    }

    private fun showAdminsScreen() {
        (parentFragment as? MainChatFragment)?.showAddAdminScreen(idChat)
    }

    private fun showMembersScreen() {
        (parentFragment as? MainChatFragment)?.showMembersScreen(idChat)
    }

    private fun showTimesScreen() {
        (parentFragment as? MainChatFragment)?.showTimeEventScreen(fromManageEventScreen=true)
    }

    private fun methodRequiresTwoPermission() {
        val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        if (EasyPermissions.hasPermissions(context!!, coarseLocation, fineLocation)) {
            showChooseMapScreen()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.this_app_needs_location_access),
                2,
                coarseLocation,
                fineLocation
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

        if (requestCode == 2) {
            showChooseMapScreen()
        }
    }

    private fun showChooseMapScreen() {
        (parentFragment as? MainChatFragment)?.showChooseMapScreen(fromManageEventScreen=true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data?.data

        if (uri != null) {
            val input = context?.contentResolver?.openInputStream(uri)
            if (input != null) {
                val file = StreamUtil.stream2file(input)
                bytes = file.readBytes()
                bytes?.let {
                    mimeType = getMimeType(it)
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    setAvatar(bitmap)
                }
            }
        }
    }

    override fun setAvatar(avatar: Bitmap?) {
        avatarIv?.setImageBitmap(avatar)
    }

    private fun chooseFile() {
        var choosePhoto = Intent(Intent.ACTION_GET_CONTENT)
        choosePhoto.type = "image/*"
        choosePhoto = Intent.createChooser(choosePhoto, getString(R.string.select_picture))
        startActivityForResult(choosePhoto, 1)
    }

    private fun getMimeType(data: ByteArray): String? {
        return try {
            val inputStream = BufferedInputStream(ByteArrayInputStream(data))
            URLConnection.guessContentTypeFromStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    override fun showName(name: String?) {
        nameEt?.setText(name)
    }

    override fun showDescription(description: String) {
        descriptionEt?.setText(description)
    }

    override fun showChatInfo() {
        fragmentManager?.popBackStack()
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    override fun showOccupantsCount(text: String) {
        occupantsCountTv?.text = text
    }

    override fun showAdminsCount(text: String) {
        adminsCountTv?.text = text
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    override fun showAdress(location: LatLng) {
        locationValueTv?.text = getAddress(location)
    }

    private fun getAddress(location: LatLng?): String {
        if (location == null) return "" + getString(R.string.no_information_available) + ""

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
        return "" + getString(R.string.no_information_available) + ""
    }

    override fun showChatsScreen(){
        (parentFragment as? MainChatFragment)?.moveAndClearPopBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
    }
}
