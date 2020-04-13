package io.moonshard.moonshard.ui.fragments.mychats.chat.info.chat

import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.chat.info.ChatInfoPresenter
import io.moonshard.moonshard.presentation.view.chat.info.ChatInfoView
import io.moonshard.moonshard.ui.adapters.chat.MemberListener
import io.moonshard.moonshard.ui.adapters.chat.MembersAdapter
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_chat_info.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.muc.Occupant
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
        var idChat = ""
        arguments?.let {
            idChat = it.getString("chatId")
            presenter.getMembers(idChat)
        }

        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        changeChatInfoBtn?.setSafeOnClickListener {
            showManageChatScreen(idChat)
        }

        leaveLayout?.setSafeOnClickListener {
            presenter.leaveGroup(idChat)
        }

        addNewMember?.setSafeOnClickListener {
            showInviteNewUserScreen(idChat)
        }
    }

    override fun hideDescription(){
        descriptionLayout?.visibility = View.GONE
    }

   override fun showChangeChatButton(isShow:Boolean){
        if(isShow){
            changeChatInfoBtn?.visibility = View.VISIBLE
        }else{
            changeChatInfoBtn?.visibility = View.GONE
        }
    }

    private fun showInviteNewUserScreen(idChat: String) {
        (parentFragment as? MainChatFragment)?.showInviteNewUserScreen(idChat)
    }

    private fun showManageChatScreen(idChat: String) {
        (parentFragment as? MainChatFragment)?.showManageChatScreen(idChat)
    }

    fun showProfileUser(jid: String) {
        (parentFragment as? MainChatFragment)?.showProfileUserScreen(jid)
    }

    override fun showMembers(members: List<Occupant>) {
        if(members.isEmpty()){
            hideLine()
        }
        (membersInfoRv?.adapter as MembersAdapter).setMembers(members)
    }

    private fun initAdapter() {
        membersInfoRv?.layoutManager = LinearLayoutManager(context)
        membersInfoRv?.adapter = MembersAdapter(object : MemberListener {
            override fun clickMember(member: String) {
                showProfileUser(member)
            }

            override fun remove(member: Occupant) {

            }
        }, arrayListOf(),false)
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun showChatsScreen() {
        fragmentManager?.popBackStack()
        fragmentManager?.popBackStack()
    }

    override fun showData(
        name: String, occupantsCount: Int,
        onlineMembersValue: Int, latLngLocation: LatLng?,
        category: String, description: String
    ) {
        //val location = getAddress(latLngLocation)
        val distance = calculationByDistance(latLngLocation)

        groupNameInfoContentTv?.text = name
        valueMembersInfoTv?.text = "$occupantsCount " + getString(R.string.members) + ", $onlineMembersValue " + getString(R.string.online)
        locationValueInfoTv?.text = distance
        // address?.text = location
        //categoryInfoTv?.text = category
        descriptionInfoTv?.text = description

        if(description.isBlank()){
            hideDescription()
        }
    }

    override fun setAvatar(avatar: Bitmap?) {
        MainApplication.getMainUIThread().post {
            profileImage?.setImageBitmap(avatar)
        }
    }

    private fun getAddress(location: LatLng?): String {
        if (location == null) return getString(R.string.no_information_available)

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
        return getString(R.string.no_information_available)
    }

   override fun hideLine(){
       viewAddUser?.visibility = View.GONE
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
                ).toInt()).toString() + getString(R.string.meters)
            } else {
                (SphericalUtil.computeDistanceBetween(
                    latLng,
                    LatLng(myLat, myLng)
                ).toInt() / 1000).toString() + getString(R.string.km)
            }
        }
        return ""
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}
