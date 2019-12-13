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
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.presentation.presenter.create_group.CreateNewEventPresenter
import io.moonshard.moonshard.presentation.view.CreateNewEventView
import io.moonshard.moonshard.ui.adapters.CategoriesAdapter
import io.moonshard.moonshard.ui.adapters.CategoryListener
import io.moonshard.moonshard.ui.fragments.map.MapFragment
import kotlinx.android.synthetic.main.fragment_create_new_event.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class CreateNewEventFragment : MvpAppCompatFragment(), CreateNewEventView {

    @InjectPresenter
    lateinit var presenter: CreateNewEventPresenter

    val dateAndTime = Calendar.getInstance()


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
        ChooseChatRepository.date = dateAndTime
        setDate(dateAndTime.get(Calendar.DAY_OF_MONTH),dateAndTime.get(Calendar.MONTH))

        initAdapter()

        if (ChooseChatRepository.address.isEmpty()) {
            address?.text = MainApplication.getAdress()
            ChooseChatRepository.lat = MainApplication.getCurrentLocation()?.latitude
            ChooseChatRepository.lng = MainApplication.getCurrentLocation()?.longitude
        } else {
            address?.text = ChooseChatRepository.address
        }

        if (ChooseChatRepository.time.isEmpty()) {
            timeTv?.text = "6 часов"
            ChooseChatRepository.time = "6 часов"
        } else {
            timeTv?.text = ChooseChatRepository.time
        }

        timesLayout?.setOnClickListener {
            ChooseChatRepository.name = nameTv?.text.toString()
            showTimesScreen()
        }

        location?.setOnClickListener {
            methodRequiresTwoPermission()
        }

        dateLayout?.setOnClickListener {
            DatePickerDialog(
                activity!!, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        nameTv?.setText(ChooseChatRepository.name)

        newChat?.setOnClickListener {
            presenter.createGroupChat(
                nameTv?.text.toString(),
                ChooseChatRepository.lat,
                ChooseChatRepository.lng,
                ChooseChatRepository.getTimeSec(),
                ChooseChatRepository.category
            )
        }

        back?.setOnClickListener {
            fragmentManager?.popBackStack()
            ChooseChatRepository.clean()
        }

        presenter.getCategories()
    }

    fun setDate(dayOfMonth: Int, month: Int) {
        when (month) {
            0 -> {
                dateTv.text = "$dayOfMonth января"
            }
            1 -> {
                dateTv.text = "$dayOfMonth февраля"

            }
            2 -> {
                dateTv.text = "$dayOfMonth марта"

            }
            3 -> {
                dateTv.text = "$dayOfMonth апреля"

            }
            4 -> {
                dateTv.text = "$dayOfMonth мая"

            }
            5 -> {
                dateTv.text = "$dayOfMonth июня"

            }
            6 -> {
                dateTv.text = "$dayOfMonth июля"

            }
            7 -> {
                dateTv.text = "$dayOfMonth августа"

            }
            8 -> {
                dateTv.text = "$dayOfMonth сенятбря"

            }
            9 -> {
                dateTv.text = "$dayOfMonth октября"

            }
            10 -> {
                dateTv.text = "$dayOfMonth ноября"
            }
            11 -> {
                dateTv.text = "$dayOfMonth декабря"

            }
        }
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
                "This app needs access to your location",
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

    fun showChooseMapScreen() {
        ChooseChatRepository.name = nameTv?.text.toString()
        val chatFragment =
            ChooseMapFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(io.moonshard.moonshard.R.id.container, chatFragment, "ChooseMapFragment")
            ?.addToBackStack("ChooseMapFragment")
            ?.commit()
    }

    private fun initAdapter() {
        categoriesRv?.layoutManager = LinearLayoutManager(context)
        categoriesRv?.adapter = CategoriesAdapter(object : CategoryListener {
            override fun clickChat(categoryName: io.moonshard.moonshard.models.api.Category) {
                ChooseChatRepository.category = categoryName
            }
        }, arrayListOf())
    }

    override fun showCategories(categories: ArrayList<io.moonshard.moonshard.models.api.Category>) {
        (categoriesRv?.adapter as? CategoriesAdapter)?.updateCategories(categories)
    }

    override fun showMapScreen() {
        val mapFragment = MapFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(io.moonshard.moonshard.R.id.container, mapFragment, null)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    private fun showTimesScreen() {
        val chatFragment =
            TimeEventFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(io.moonshard.moonshard.R.id.container, chatFragment, "TimeEventFragment")
            ?.addToBackStack("TimeEventFragment")
            ?.commit()
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}
