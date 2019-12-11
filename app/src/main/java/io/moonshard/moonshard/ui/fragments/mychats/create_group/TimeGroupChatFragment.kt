package io.moonshard.moonshard.ui.fragments.mychats.create_group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.ui.adapters.RvTimeListener
import io.moonshard.moonshard.ui.adapters.TimeGroupChatAdapter
import kotlinx.android.synthetic.main.fragment_time_group_chat.*


class TimeGroupChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_group_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back?.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        val times = arrayListOf<String>()
        times.add("6 часов")
        times.add("12 часов")
        times.add("24 часа")
        times.add("3 дня")
        times.add("1 неделю")

        timesRv?.layoutManager = LinearLayoutManager(view.context)
        timesRv?.adapter = TimeGroupChatAdapter(object : RvTimeListener {
            override fun clickChat(time: String) {
                ChooseChatRepository.time = time
            }
        }, times)
    }
}
