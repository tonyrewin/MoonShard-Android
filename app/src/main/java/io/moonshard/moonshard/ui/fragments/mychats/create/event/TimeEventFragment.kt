package io.moonshard.moonshard.ui.fragments.mychats.create.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.ui.adapters.RvTimeListener
import io.moonshard.moonshard.ui.adapters.TimeGroupChatAdapter
import kotlinx.android.synthetic.main.fragment_time_group_chat.*


class TimeEventFragment : Fragment() {

    private var fromManageEventScreen: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_group_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            fromManageEventScreen = it.getBoolean("fromManageEventScreen", false)
        }

        back?.setSafeOnClickListener {
            fragmentManager?.popBackStack()
        }

        val times = arrayListOf<String>()
        times.add("1 " + getString(R.string.day) + "")
        times.add("2 " + getString(R.string.days234) + "")
        times.add("3 " + getString(R.string.days234) + "")
        times.add("4 " + getString(R.string.days234) + "")
        times.add("5 " + getString(R.string.days) + "")
        times.add("6 " + getString(R.string.days) + "")
        times.add("" + getString(R.string.a_week) + "")

        timesRv?.layoutManager = LinearLayoutManager(view.context)
        timesRv?.adapter = TimeGroupChatAdapter(object : RvTimeListener {
            override fun clickChat(time: String) {
                if(fromManageEventScreen){
                    ChangeEventRepository.event?.ttl = convertDaysToTtl(time)
                }else{
                    ChooseChatRepository.time = time
                }
            }
        }, times)
    }

    fun convertDaysToTtl(days:String):Long{
        var ttl:Long = 60*60*24
        when (days) {
            "1 " + getString(R.string.day) + "" -> ttl = 60*60*24
            "2 " + getString(R.string.days234) + "" -> ttl = 60*60*48
            "3 " + getString(R.string.days234) + "" -> ttl = 60*60*(24*3)
            "4 " + getString(R.string.days234) + "" -> ttl = 60*60*(24*4)
            "5 " + getString(R.string.days) + "" -> ttl = 60*60*(24*5)
            "6 " + getString(R.string.days) + "" -> ttl = 60*60*(24*6)
            "" + getString(R.string.a_week) + "" -> ttl = 60*60*(24*7)
        }
        return ttl
    }
}
