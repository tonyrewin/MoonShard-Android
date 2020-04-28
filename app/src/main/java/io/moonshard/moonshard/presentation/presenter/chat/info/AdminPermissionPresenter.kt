package io.moonshard.moonshard.presentation.presenter.chat.info

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.BasePresenter
import io.moonshard.moonshard.models.AdminPermission
import io.moonshard.moonshard.presentation.view.chat.info.AdminPermissionView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class AdminPermissionPresenter: MvpPresenter<AdminPermissionView>() {

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

}