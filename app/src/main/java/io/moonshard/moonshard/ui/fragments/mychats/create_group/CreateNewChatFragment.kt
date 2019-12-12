package io.moonshard.moonshard.ui.fragments.mychats.create_group

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.Category
import io.moonshard.moonshard.presentation.presenter.create_group.CreateNewChatPresenter
import io.moonshard.moonshard.presentation.view.CreateNewChatView
import io.moonshard.moonshard.ui.adapters.CategoriesAdapter
import io.moonshard.moonshard.ui.adapters.CategoryListener
import io.moonshard.moonshard.ui.fragments.map.MapFragment
import kotlinx.android.synthetic.main.fragment_create_new_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import pub.devrel.easypermissions.EasyPermissions

class CreateNewChatFragment : MvpAppCompatFragment(), CreateNewChatView {

    @InjectPresenter
    lateinit var presenter: CreateNewChatPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_new_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        if (ChooseChatRepository.address.isEmpty()) {
            address?.text = MainApplication.getAdress()
            ChooseChatRepository.lat =  MainApplication.getCurrentLocation().latitude
            ChooseChatRepository.lng =  MainApplication.getCurrentLocation().longitude
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
        val chatFragment = ChooseMapFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "ChooseMapFragment")
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

    private fun initCategories(): ArrayList<Category> {
        val categoryOne = Category(R.drawable.ic_star, "Тусовки")
        val categoryTwo = Category(R.drawable.ic_case, "Бизнес ивенты")
        val categoryThree = Category(R.drawable.ic_heart, "Кружок по интересам")
        val categoryFour = Category(R.drawable.ic_culture_category, "Культурные мероприятия")

        val categories = arrayListOf<Category>()
        categories.add(categoryOne)
        categories.add(categoryTwo)
        categories.add(categoryThree)
        categories.add(categoryFour)

        return categories
    }

    override fun showMapScreen() {
        val mapFragment = MapFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, mapFragment, null)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    private fun showTimesScreen() {
        val chatFragment = TimeGroupChatFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "TimeGroupChatFragment")
            ?.addToBackStack("TimeGroupChatFragment")
            ?.commit()
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}
