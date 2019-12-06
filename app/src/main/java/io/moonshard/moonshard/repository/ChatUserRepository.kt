package io.moonshard.moonshard.repository

import io.moonshard.moonshard.ObjectBox
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.models.dbEntities.ChatEntity_
import io.moonshard.moonshard.models.dbEntities.ChatUser
import io.moonshard.moonshard.models.dbEntities.MessageEntity
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

object ChatUserRepository {

    private val chatBox: Box<ChatEntity> = ObjectBox.boxStore.boxFor()



}