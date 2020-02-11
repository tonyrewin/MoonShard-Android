package io.moonshard.moonshard.ui.fragments.mychats.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.fragments.mychats.create.event.CreateNewEventFragment
import kotlinx.android.synthetic.main.fragment_add_new_event.*


class AddNewEventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startLocalGroup?.setSafeOnClickListener {
            showCreateNewChatScreen()
        }
    }
    fun showCreateNewChatScreen(){
        (activity as? MainActivity)?.showCreateNewEventScreen()
    }
}
