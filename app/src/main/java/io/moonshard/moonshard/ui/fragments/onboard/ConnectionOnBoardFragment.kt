package io.moonshard.moonshard.ui.fragments.onboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import de.adorsys.android.securestoragelibrary.SecurePreferences

import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.activities.RegisterActivity
import kotlinx.android.synthetic.main.fragment_connection_on_board.*


class ConnectionOnBoardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection_on_board, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipBtn?.setOnClickListener {
            showRegistrationScreen()
        }

        nextBtn?.setOnClickListener {
            showRegistrationScreen()
        }
    }


    private fun showRegistrationScreen() {
        SecurePreferences.setValue("first_start", false)
        val intent = Intent(activity, RegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        startActivity(intent)
    }


    fun newInstance(): ConnectionOnBoardFragment {
        return ConnectionOnBoardFragment()
    }
}
