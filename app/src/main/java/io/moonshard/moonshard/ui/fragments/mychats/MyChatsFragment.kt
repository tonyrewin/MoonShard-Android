package io.moonshard.moonshard.ui.fragments.mychats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.Utils.hideKeyboard
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.chats.MyChatsPagerAdapter
import io.moonshard.moonshard.ui.fragments.mychats.create.AddChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.CreateGroupFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_my_chats.*


class MyChatsFragment : Fragment() {

    private var disposible: Disposable? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()

        cancelBtn?.setOnClickListener {
            hideSearch()
        }

        disposible = findEd.afterTextChangeEvents()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
               // (chatsRv?.adapter as? ChatListAdapter)?.presenter?.setFilter(it.editable?.toString() ?: "")
            }

        find?.setOnClickListener {
            if (searchLayoutToolbar?.visibility == View.GONE) {
                showSearch()
            } else {
                hideSearch()
            }
        }

        newChat?.setOnClickListener {
            (activity as MainActivity).hideBottomNavigationBar()
            val newFragment = CreateGroupFragment()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.container, newFragment,"CreateGroupFragment")?.addToBackStack("CreateGroupFragment")
                ?.commit()
        }
    }


    private fun showSearch() {
        searchLayoutToolbar?.visibility = View.VISIBLE
        defaultToolbar?.visibility = View.GONE
        (activity as? MainActivity)?.hideBottomNavigationBar()
    }

    private fun hideSearch() {
        hideKeyboard(activity!!)
            // (chatsRv?.adapter as? ChatListAdapter)?.presenter?.setFilter("")
        searchLayoutToolbar?.visibility = View.GONE
        defaultToolbar?.visibility = View.VISIBLE
        (activity as? MainActivity)?.showBottomNavigationBar()
    }

    private fun initViewPager(){
        tabLayout.setupWithViewPager(viewPager)
        val sectionsPagerAdapter = MyChatsPagerAdapter(
            childFragmentManager,
            context!!,
            arrayOf(MyChatsPagerAdapter.TabItem.CHATS,MyChatsPagerAdapter.TabItem.RECOMMENDATIONS)
        )
        viewPager?.adapter = sectionsPagerAdapter
    }
}
