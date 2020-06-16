package io.moonshard.moonshard.ui.fragments.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.contracts.TicketSale721.CalculatedFeesEventResponse
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.presenter.profile.ProfilePresenter
import io.moonshard.moonshard.presentation.view.profile.ProfileView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import retrofit2.HttpException


class ProfileFragment : MvpAppCompatFragment(),
    ProfileView {

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavigationBar()
        presenter.getInfoProfile()
        presenter.getAvatar()
        presenter.getVerificationEmail()

        profileSettingsLayout?.setSafeOnClickListener {
            showChangeProfileScreen()
        }

        myTicketBtn?.setSafeOnClickListener {
            (activity as MainActivity).showMyTicketsFragment()
        }
        presentTicketLayout?.setSafeOnClickListener {
            (activity as MainActivity).showPresentTicketFragment()
        }
    }

    private fun showChangeProfileScreen() {
        val fragment =
            ChangeProfileFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, fragment, "ChangeProfileFragment")
            ?.addToBackStack("ChangeProfileFragment")
            ?.commit()
    }

    override fun setData(nickName: String?, description: String?, jidPart: String?) {
        MainApplication.getMainUIThread().post {
            nameTv?.text = nickName ?: "Имя"
            if (!jidPart.isNullOrBlank()) {
                myJid?.text = "@$jidPart"
            } else {
                myJid?.text = "jid"
            }
            descriptionTv?.text = description
        }
    }

    override fun setAvatar(avatar: Bitmap?) {
        MainApplication.getMainUIThread().post {
            avatarIv?.setImageBitmap(avatar)
        }
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun setVerification(email: String?, isActivated: Boolean?) {
        if (email.isNullOrEmpty()) {
            walletBtn?.setSafeOnClickListener {
                (activity as MainActivity).showVerificationEmailScreen()
            }
        } else {
            if (isActivated!!) {
                walletBtn?.setSafeOnClickListener {
                    (activity as MainActivity).showWalletFragment()
                }
            } else {
                walletBtn?.setSafeOnClickListener {
                    (activity as MainActivity).showVerificationEmailScreen()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
    }

}
