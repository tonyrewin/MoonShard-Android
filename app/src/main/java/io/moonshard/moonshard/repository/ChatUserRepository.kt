package io.moonshard.moonshard.repository

import io.moonshard.moonshard.ObjectBox
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.models.dbEntities.ChatUser
import io.moonshard.moonshard.models.dbEntities.ChatUser_
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.rx.RxQuery
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.jxmpp.jid.Jid

object ChatUserRepository {
    private val chatUsers: Box<ChatUser> = ObjectBox.boxStore.boxFor()

    fun saveUser(chatUser: ChatUser): Completable {
        return Completable.create {
            chatUsers.put(chatUser)
            it.onComplete()
        }
    }

    fun getUserAsObservable(jid: Jid): Observable<ChatUser> {
        return Observable.create {
            val userQuery = chatUsers.query {
                equal(ChatUser_.jid, jid.asUnescapedString())
            }
            RxQuery.observable(userQuery).subscribe({ user ->
                if (!it.isDisposed) {
                    if (user.isNotEmpty()) {
                        it.onNext(user.first())
                    } else {
                        it.onError(NotFoundException())
                    }
                }
            }, { ex ->
                if (!it.isDisposed) {
                    it.onError(ex)
                }
            })
        }
    }

    fun getUserAsSingle(jid: Jid): Single<ChatUser> {
        return Single.create {
            val userQuery = chatUsers.query {
                equal(ChatUser_.jid, jid.asUnescapedString())
            }
            RxQuery.single(userQuery).subscribe({ user ->
                if (!it.isDisposed) {
                    if (user.isNotEmpty()) {
                        it.onSuccess(user.first())
                    } else {
                        it.onError(NotFoundException())
                    }
                }
            }, { ex ->
                if (!it.isDisposed) {
                    it.onError(ex)
                }
            })
        }
    }

    fun removeUser(jid: Jid): Completable {
        return Completable.create {
            chatUsers.query {
                equal(ChatUser_.jid, jid.asUnescapedString())
            }.remove()
            it.onComplete()
        }
    }

    fun clearUsers(): Completable {
        return Completable.create {
            chatUsers.removeAll()
            it.onComplete()
        }
    }
}