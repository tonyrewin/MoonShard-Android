package io.moonshard.moonshard.ui.fragments.chat

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.chat.ChatInfoPresenter
import io.moonshard.moonshard.presentation.view.chat.ChatInfoView
import io.moonshard.moonshard.ui.adapters.chat.MemberListener
import io.moonshard.moonshard.ui.adapters.chat.MembersAdapter
import kotlinx.android.synthetic.main.fragment_chat_info.*
import kotlinx.android.synthetic.main.fragment_members_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.muc.Affiliate
import java.io.IOException
import java.util.*


class ChatInfoFragment : MvpAppCompatFragment(), ChatInfoView {

    @InjectPresenter
    lateinit var presenter: ChatInfoPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        arguments?.let {
            val idChat = it.getString("chatId")
            presenter.getMembers(idChat!!)
        }
    }

    override fun showMembers(members: List<Affiliate>) {
        (membersInfoRv?.adapter as MembersAdapter).setMembers(members)
    }

    private fun initAdapter() {
        membersInfoRv?.layoutManager = LinearLayoutManager(context)
        membersInfoRv?.adapter = MembersAdapter(object : MemberListener {
            override fun remove(categoryName: String) {

            }
        }, arrayListOf())
    }

    override fun showData(
        name: String, occupantsCount: Int,
        onlineMembersValue: Int, latLngLocation: LatLng?,
        category: String, description: String) {
        val location = getAddress(latLngLocation)
        val distance = calculationByDistance(latLngLocation)

        groupNameInfoContentTv?.text = name
        valueMembersInfoTv?.text = "$occupantsCount участников, $onlineMembersValue онлайн"
        locationValueInfoTv?.text = distance
        address?.text = location
        categoryInfoTv?.text = category
        descriptionInfoTv?.text = description
    }

    private fun getAddress(location: LatLng?): String {
        if (location == null) return "Информация отсутствует"

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

    private fun calculationByDistance(latLng: LatLng?): String {
        if (latLng == null) return ""

        MainApplication.getCurrentLocation()?.let {
            val myLat = MainApplication.getCurrentLocation().latitude
            val myLng = MainApplication.getCurrentLocation().longitude

            val km = SphericalUtil.computeDistanceBetween(
                latLng,
                LatLng(myLat, myLng)
            ).toInt() / 1000
            return if (km < 1) {
                (SphericalUtil.computeDistanceBetween(
                    latLng, LatLng(myLat, myLng)
                ).toInt()).toString() + " метрах"
            } else {
                (SphericalUtil.computeDistanceBetween(
                    latLng,
                    LatLng(myLat, myLng)
                ).toInt() / 1000).toString() + " км"
            }
        }
        return ""
    }
}
