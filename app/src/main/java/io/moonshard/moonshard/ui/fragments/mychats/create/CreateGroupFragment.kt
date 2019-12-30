package io.moonshard.moonshard.ui.fragments.mychats.create

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.adapters.chats.MyChatsPagerAdapter
import kotlinx.android.synthetic.main.fragment_create_group.*


class CreateGroupFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()

        back?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    private fun initViewPager(){
        tabLayout.setupWithViewPager(viewPager)
        val sectionsPagerAdapter = MyChatsPagerAdapter(
            childFragmentManager,
            context!!,
            arrayOf(MyChatsPagerAdapter.TabItem.CREATE_CHAT,MyChatsPagerAdapter.TabItem.CREATE_EVENT)
        )
        viewPager?.adapter = sectionsPagerAdapter
    }
}
