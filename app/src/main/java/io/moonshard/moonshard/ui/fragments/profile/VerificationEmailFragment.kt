package io.moonshard.moonshard.ui.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.profile.VerificationEmailPresenter
import io.moonshard.moonshard.presentation.view.profile.VerificationEmailView
import kotlinx.android.synthetic.main.fragment_verification_email.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class VerificationEmailFragment  : MvpAppCompatFragment(),
    VerificationEmailView {

    @InjectPresenter
    lateinit var presenter: VerificationEmailPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendLinkBtn?.setSafeOnClickListener {
            presenter.verificationEmail(emailEt.text.toString())
        }

        back?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        readyBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        closeBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    override fun showVerificationLayout(isConfirmed:Boolean){
        if(isConfirmed){
            readyBtn?.visibility = View.VISIBLE
            notConfirmedLayout.visibility  = View.GONE
            emailConfirmed.visibility = View.VISIBLE
        }else{
            readyBtn?.visibility = View.GONE
            notConfirmedLayout.visibility  = View.VISIBLE
            emailConfirmed.visibility = View.GONE
        }
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    override fun back(){
        fragmentManager?.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        presenter.getVerificationEmail()
    }
}
