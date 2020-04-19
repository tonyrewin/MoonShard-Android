package io.moonshard.moonshard.ui.fragments.mychats.create.event

import android.Manifest
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.presenter.create_group.CreateNewEventPresenter
import io.moonshard.moonshard.presentation.view.CreateNewEventView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.CategoriesAdapter
import io.moonshard.moonshard.ui.adapters.CategoryListener
import io.moonshard.moonshard.ui.adapters.create.GroupsAdapter
import io.moonshard.moonshard.ui.adapters.create.GroupsListener
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_create_new_event.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class CreateNewEventFragment : MvpAppCompatFragment(), CreateNewEventView {

    @InjectPresenter
    lateinit var presenter: CreateNewEventPresenter

    val dateAndTime = Calendar.getInstance()

    var fromEventsFragment: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            io.moonshard.moonshard.R.layout.fragment_create_new_event,
            container,
            false
        )
    }

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(Calendar.YEAR, year)
            dateAndTime.set(Calendar.MONTH, monthOfYear)
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            setDate(dayOfMonth, monthOfYear)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            fromEventsFragment = it.getBoolean("fromEventsFragment", false)
        }

        ChooseChatRepository.date = dateAndTime
        setDate(dateAndTime.get(Calendar.DAY_OF_MONTH), dateAndTime.get(Calendar.MONTH))

        initAdapter()

        if (ChooseChatRepository.address.isEmpty()) {
            address?.text = MainApplication.getAddress()
            ChooseChatRepository.address = MainApplication.getAddress()
            ChooseChatRepository.lat = MainApplication.getCurrentLocation()?.latitude
            ChooseChatRepository.lng = MainApplication.getCurrentLocation()?.longitude
        } else {
            address?.text = ChooseChatRepository.address
        }

        if (ChooseChatRepository.time.isEmpty()) {
            timeTv?.text = "1 " + getString(R.string.day)
            ChooseChatRepository.time = "1 " + getString(R.string.day)
        } else {
            timeTv?.text = ChooseChatRepository.time
        }

        timesLayout?.setSafeOnClickListener {
            ChooseChatRepository.name = nameTv?.text.toString()
            showTimesScreen()
        }

        location?.setSafeOnClickListener {
            methodRequiresTwoPermission()
        }

        dateLayout?.setSafeOnClickListener {
            DatePickerDialog(
                activity!!, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        nameTv?.setText(ChooseChatRepository.name)

        newChat?.setSafeOnClickListener {
            presenter.createGroupChat(
                nameTv?.text.toString(),
                ChooseChatRepository.lat,
                ChooseChatRepository.lng,
                60*60*24, // TODO: start with default value?
                ChooseChatRepository.category,
                ChooseChatRepository.group,
                ChooseChatRepository.getEventStartDate(),
                ChooseChatRepository.address
            )
        }

        back?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
            ChooseChatRepository.clean()
        }

        presenter.getCategories()

        presenter.getRooms()
    }

    fun setDate(dayOfMonth: Int, month: Int) {
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
        ChooseChatRepository.name = nameTv?.text.toString()
        if (fromEventsFragment) {
            (parentFragment as? MainChatFragment)?.showChooseMapScreen()
        } else {
            (activity as? MainActivity)?.showChooseMapScreen()
        }
    }

    private fun initAdapter() {
        categoriesRv?.layoutManager = LinearLayoutManager(context)
        categoriesRv?.adapter = CategoriesAdapter(object : CategoryListener {
            override fun clickChat(categoryName: io.moonshard.moonshard.models.api.Category) {
                ChooseChatRepository.category = categoryName
            }
        }, arrayListOf())


        groupsRv?.layoutManager = LinearLayoutManager(context)
        groupsRv?.adapter = GroupsAdapter(object : GroupsListener {
            override fun clickChat(categoryName: ChatEntity) {
                ChooseChatRepository.group = categoryName
            }
        }, arrayListOf())
    }

    override fun showCategories(categories: ArrayList<io.moonshard.moonshard.models.api.Category>) {
        (categoriesRv?.adapter as? CategoriesAdapter)?.updateCategories(categories)
    }

    override fun showAdminChats(chats: ArrayList<ChatEntity>) {
        if(chats.isEmpty()){
            groupsLayout.visibility = View.GONE
        }else{
            groupsLayout.visibility = View.VISIBLE
            (groupsRv?.adapter as? GroupsAdapter)?.updateGroups(chats)
        }
    }

    override fun showMapScreen() {
        (activity as? MainActivity)?.showMapScrenFromCreateNewEventScreen()
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    private fun showTimesScreen() {
        if (fromEventsFragment) {
            (parentFragment as? MainChatFragment)?.showTimeEventScreen()
        } else {
            (activity as? MainActivity)?.showTimeEventScreen()
        }
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}
