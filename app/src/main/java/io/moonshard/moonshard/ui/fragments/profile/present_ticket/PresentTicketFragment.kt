package io.moonshard.moonshard.ui.fragments.profile.present_ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_present_ticket.*


class PresentTicketFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_present_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideBottomNavigationBar()

        ticket?.setOnClickListener {
            (activity as MainActivity).showTypeTicketsPresentFragment()
        }

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }
}
