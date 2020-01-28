package io.moonshard.moonshard.ui.fragments.settings

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.settings.SecurityPresenter
import io.moonshard.moonshard.presentation.view.settings.SecurityView
import io.moonshard.moonshard.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_security.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class SecurityFragment : MvpAppCompatFragment(), SecurityView {

    @InjectPresenter
    lateinit var presenter: SecurityPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigationBar()

        readBtn?.setSafeOnClickListener {
            presenter.changePassword(newPassEt.text.toString(), repeatNewPassEt.text.toString(),currentPassEt.text.toString())
        }
        showOrHidePassword()


        backBtn?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()

    }

    override fun showSettingsScreen() {
        fragmentManager?.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).showBottomNavigationBar()
    }

    private fun showOrHidePassword(){
        var isSecurityCurrent=true
        visibleCurrentPassBtn?.setSafeOnClickListener {
            if(isSecurityCurrent){
                currentPassEt?.transformationMethod = null
                visibleCurrentPassBtn?.setImageResource(R.drawable.ic_pass_on)
                isSecurityCurrent = false
            }else{
                currentPassEt?.transformationMethod = PasswordTransformationMethod()
                visibleCurrentPassBtn?.setImageResource(R.drawable.ic_pass_off)
                isSecurityCurrent = true
            }
        }

        var isSecurity = true
        visibleNewPassBtn?.setSafeOnClickListener {
            if (isSecurity) {
                newPassEt?.transformationMethod = null
                visibleNewPassBtn?.setImageResource(R.drawable.ic_pass_on)
                isSecurity = false
            }else{
                newPassEt?.transformationMethod = PasswordTransformationMethod()
                visibleNewPassBtn?.setImageResource(R.drawable.ic_pass_off)
                isSecurity = true
            }
        }

        var isSecurityRepeat=true
        visibleRepeatNewPassBtn?.setSafeOnClickListener {
            if (isSecurityRepeat) {
                repeatNewPassEt?.transformationMethod = null
                visibleRepeatNewPassBtn?.setImageResource(R.drawable.ic_pass_on)
                isSecurityRepeat = false
            }else{
                repeatNewPassEt?.transformationMethod = PasswordTransformationMethod()
                visibleRepeatNewPassBtn?.setImageResource(R.drawable.ic_pass_off)
                isSecurityRepeat = true
            }
        }
    }
}
