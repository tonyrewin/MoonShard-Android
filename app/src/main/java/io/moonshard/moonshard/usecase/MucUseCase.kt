package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.MainApplication
import io.reactivex.Single
import org.jivesoftware.smackx.muc.RoomInfo
import org.jxmpp.jid.impl.JidCreate

class MucUseCase {
     fun getRoomInfo(jid: String): Single<RoomInfo> {
        return Single.create {
            try {
                val groupId = JidCreate.entityBareFrom(jid)
                val muc =
                    MainApplication.getXmppConnection().multiUserChatManager
                        .getMultiUserChat(groupId)
                val info =
                    MainApplication.getXmppConnection().multiUserChatManager
                        .getRoomInfo(muc.room)
                it.onSuccess(info)
            } catch (e: java.lang.Exception) {
                it.onError(e)
            }
        }
    }
}