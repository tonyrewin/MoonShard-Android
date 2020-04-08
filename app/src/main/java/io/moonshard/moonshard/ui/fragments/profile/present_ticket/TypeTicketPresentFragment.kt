package io.moonshard.moonshard.ui.fragments.profile.present_ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import kotlinx.android.synthetic.main.fragment_type_ticket_present.*
import kotlinx.android.synthetic.main.little_ticket_item.view.*


class TypeTicketPresentFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type_ticket_present, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        littleTicket?.arrowImageView?.visibility = View.VISIBLE

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }
}
