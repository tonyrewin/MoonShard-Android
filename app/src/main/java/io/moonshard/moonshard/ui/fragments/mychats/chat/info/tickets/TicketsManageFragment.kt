package io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import kotlinx.android.synthetic.main.fragment_tickets_manage.*
import java.util.*


class TicketsManageFragment : Fragment() {

    var idChat = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tickets_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            idChat = it.getString("chatId")
        }

        scanTicketsBtn?.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }

        addTicketsBtn?.setOnClickListener {
            (parentFragment as? MainChatFragment)?.showManageTypesTicketScreen(idChat)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity!!, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity!!, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
