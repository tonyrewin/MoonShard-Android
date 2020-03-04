package io.moonshard.moonshard.ui.fragments.mychats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.Utils.hideKeyboard
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.view.chat.MyChatsView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.chats.MyChatsPagerAdapter
import io.moonshard.moonshard.ui.fragments.mychats.create.CreateGroupFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_my_chats.*
import moxy.MvpAppCompatFragment
import org.jivesoftware.smackx.search.ReportedData
import org.jivesoftware.smackx.search.UserSearchManager
import org.jxmpp.jid.impl.JidCreate


class MyChatsFragment : MvpAppCompatFragment(), MyChatsView {

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
        (activity as MainActivity).showBottomNavigationBar()

        initViewPager()

        try {
            val search = UserSearchManager(MainApplication.getXmppConnection().connection)
            val j =
                JidCreate.domainBareFrom("search." + MainApplication.getXmppConnection().connection.xmppServiceDomain)
            val searchForm = search.getSearchForm(j)
            val answerForm = searchForm.createAnswerForm()
            answerForm.setAnswer("nick", "qwe")
            var data = search.getSearchResults(answerForm, j)

            if (data.rows != null) {
                val it = data.rows as Iterator<ReportedData.Row>
                while (it.hasNext()) {
                    val row = it.next()
                    val iterator = row.getValues("jid") as Iterator<ReportedData.Row>
                    if (iterator.hasNext()) {
                        val value = iterator.next().toString()
                        com.orhanobut.logger.Logger.i("Iteartor values......", " $value")
                    }
                    //Log.i("Iteartor values......"," "+value);
                }
            }
            var kek = ""
        } catch (e: Exception) {
            var kek = ""
        }


        (activity!!.supportFragmentManager.findFragmentByTag("CreatedChatScreen"))?.let {
            activity!!.supportFragmentManager.beginTransaction().remove(it).commit()
        }

        cancelBtn?.setSafeOnClickListener {
            hideSearch()
        }

        disposible = findEd?.afterTextChangeEvents()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                try {
                    val chatsFragment =
                        childFragmentManager.findFragmentByTag("android:switcher:" + viewPager.id + ":" + 0)
                    (chatsFragment as? ChatsFragment)?.setFilter(it.editable?.toString() ?: "")

                    val recommendationsFragment = childFragmentManager.findFragmentByTag("android:switcher:" + viewPager.id + ":" + 1)
                    (recommendationsFragment as? RecommendationsFragment)?.setFilter(it.editable?.toString() ?: "")
                } catch (e: Exception) {
                    com.orhanobut.logger.Logger.d(e)
                }
            }

        find?.setSafeOnClickListener {
            if (searchLayoutToolbar?.visibility == View.GONE) {
                showSearch()
            } else {
                hideSearch()
            }
        }

        newChat?.setSafeOnClickListener {
            (activity as? MainActivity)?.hideBottomNavigationBar()
            (activity as? MainActivity)?.showCreateGroupScreen()
        }
    }

    private fun showSearch() {
        searchLayoutToolbar?.visibility = View.VISIBLE
        defaultToolbar?.visibility = View.GONE
        (activity as? MainActivity)?.hideBottomNavigationBar()
    }

    private fun hideSearch() {
        hideKeyboard(activity!!)
        findEd?.text?.clear()
        searchLayoutToolbar?.visibility = View.GONE
        defaultToolbar?.visibility = View.VISIBLE
        (activity as? MainActivity)?.showBottomNavigationBar()
    }

    private fun initViewPager() {
        tabLayout?.setupWithViewPager(viewPager)
        val sectionsPagerAdapter = MyChatsPagerAdapter(
            childFragmentManager,
            context!!,
            arrayOf(MyChatsPagerAdapter.TabItem.CHATS, MyChatsPagerAdapter.TabItem.RECOMMENDATIONS)
        )
        viewPager?.adapter = sectionsPagerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()

        for (fragment in childFragmentManager.fragments) {
            childFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
