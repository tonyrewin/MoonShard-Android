package io.moonshard.moonshard.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.jivesoftware.smackx.vcardtemp.packet.VCard


class ChangeProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            io.moonshard.moonshard.R.layout.fragment_change_profile,
            container,
            false
        )
    }

    fun test() {
        val vCard = VCard()
    }
}
