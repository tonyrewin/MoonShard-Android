package io.moonshard.moonshard.ui.fragments.onboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.ISlidePolicy
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.ui.activities.auth.RegisterActivity
import kotlinx.android.synthetic.main.fragment_acquaintance.*


class AcquaintanceFragment : Fragment(), ISlidePolicy {
    override fun isPolicyRespected(): Boolean {
        return true
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            io.moonshard.moonshard.R.layout.fragment_acquaintance,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipBtn?.setSafeOnClickListener {
            showRegistrationScreen()
        }
    }

    private fun showRegistrationScreen() {
        SecurePreferences.setValue("first_start", false)
        val intent = Intent(activity, RegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        startActivity(intent)
    }

    fun newInstance(): AcquaintanceFragment {
        return AcquaintanceFragment()
    }
}
