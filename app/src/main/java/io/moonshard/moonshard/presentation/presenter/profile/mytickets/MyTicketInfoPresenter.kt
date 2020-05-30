package io.moonshard.moonshard.presentation.presenter.profile.mytickets

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.profile.my_tickets.MyTicketInfoView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class MyTicketInfoPresenter: MvpPresenter<MyTicketInfoView>() {

    fun getEventInfo(jid: String){
        val jidEvent = JidCreate.entityBareFrom(jid)

        val muc = MainApplication.getXmppConnection().multiUserChatManager
            .getMultiUserChat(jidEvent)

        val info = MainApplication.getXmppConnection().multiUserChatManager
            .getRoomInfo(muc.room)
        viewState?.showEventInfo(info.name)

        setAvatar(jid,info.name)

    }

    @SuppressLint("CheckResult")
    private fun setAvatar(jid: String, nameChat: String) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatarForTicket(jid, nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        MainApplication.getMainUIThread().post {
                            viewState.setAvatar(avatar)
                        }
                    }
                }, { throwable ->
                    throwable.message?.let { Logger.e(it) }
                })
        }
    }
}