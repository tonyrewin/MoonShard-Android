package io.moonshard.moonshard.presentation.presenter.chat.info

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.info.AdminPermissionView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.Occupant
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class AdminPermissionPresenter: MvpPresenter<AdminPermissionView>() {

     fun getData(occupant: Occupant?,userJid: String?){
        if(occupant!=null){
            getAvatar(occupant.jid.asUnescapedString())
            viewState?.showNickName(occupant.nick.toString())
        }else{
            getAvatar(userJid!!)
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard(JidCreate.entityBareFrom(userJid))
            viewState?.showNickName(card.nickName)
        }
    }

    private fun getAvatar(jid: String) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        viewState?.setAvatar(avatar)
                    }
                }, { throwable -> Logger.d(throwable.message) })
        }
    }

    fun changeRole(type:String,jidUser: String,jidChat:String){
        when (type) {
            "Фейс контроль" -> {
                    try {
                        val groupId = JidCreate.entityBareFrom(jidChat)
                        val muc =
                            MainApplication.getXmppConnection().multiUserChatManager
                                .getMultiUserChat(groupId)

                        muc.grantMembership(JidCreate.from(jidUser))
                        viewState?.goToChatScreen()
                    } catch (e: Exception) {
                        Logger.d(e)
                }
            }
            "Администратор" -> {
                    try {
                        val groupId = JidCreate.entityBareFrom(jidChat)
                        val muc =
                            MainApplication.getXmppConnection().multiUserChatManager
                                .getMultiUserChat(groupId)
                        muc.grantAdmin(JidCreate.from(jidUser))
                        viewState?.goToChatScreen()
                    } catch (e: Exception) {
                        Logger.d(e)
                    }
            }
            else -> {

            }
        }
    }
}