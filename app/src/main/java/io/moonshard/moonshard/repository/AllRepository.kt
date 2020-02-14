package io.moonshard.moonshard.repository

import io.reactivex.Completable

object AllRepository {

    fun clearUsers(): Completable {
        return Completable.create {
            //chatUsers.removeAll()
            it.onComplete()
        }
    }

}