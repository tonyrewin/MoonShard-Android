package io.moonshard.moonshard.presentation.presenter.profile.wallet.transfer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.presentation.view.profile.wallet.transfer.TransferWalletView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log

@InjectViewState
class TransferWalletPresenter : MvpPresenter<TransferWalletView>() {

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    private var jidCurrentUser: String? = null

    init {
        useCase = AuthUseCase()
    }


    fun showRecipient(jid: String) {
        jidCurrentUser = jid
        compositeDisposable.add(getInfoFromVCard(jid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setAvatar(jid, it["nickName"]!!)
                viewState?.setDataRecipient(it["nickName"]!!, it["status"]!!)
            }, {
                it.message?.let { it1 -> viewState?.showToast(it1) }
            })
        )
    }

    private fun getInfoFromVCard(jid: String): Single<HashMap<String, String>> {
        return Single.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard(JidCreate.entityBareFrom(jid))
            val nickName = card.nickName

            val hashMapData = hashMapOf<String, String>()
            hashMapData["nickName"] = nickName
            hashMapData["status"] = "online" //todo hardcore
            it.onSuccess(hashMapData)
        }
    }

    private fun setAvatar(jid: String, nameChat: String) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        viewState?.showAvatarRecipient(avatar)
                    }
                }, { throwable ->
                    Log.e(throwable.message)
                })
        }
    }

    fun sendMoney(amount: String) {
        getWalletAddress(amount)
    }

    fun getBalance() {
        MainService.getWalletService().balance?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { balance, throwable ->
                if (throwable == null) {
                    viewState?.showBalance(balance)
                } else {
                    throwable.message?.let { viewState?.showToast(it) }
                }
            }
    }

    fun getWalletAddress(amount: String) {
        if (!jidCurrentUser.isNullOrBlank()) {
            val accessToken = getLongStringValue("accessToken")

            viewState?.showProgressBar()
            compositeDisposable.add(useCase!!.getWalletAddress(
                jidCurrentUser!!, accessToken!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, throwable ->
                    Logger.d(result)
                    if (throwable == null) {
                        Log.d("testTransferMoney: ", jidCurrentUser)
                        Log.d("testTransferMoney: ", result.walletAddress)
                        if (!result.walletAddress.isNullOrBlank()) transferMoney(
                            result.walletAddress,
                            amount
                        )
                    } else {
                        viewState?.hideProgressBar()
                        viewState?.showToast("Произошла ошибка")
                        Logger.d(result)
                    }
                })
        }
    }

    private fun transferMoney(addressTo: String, amount: String) {
        Log.d("testTransferMoney: ", amount)
        compositeDisposable.add(MainService.getBuyTicketService()
            .sendMoney(addressTo, amount.toFloat())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.hideProgressBar()
                viewState?.showSuccessScreen()
            }, {
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
                Logger.d(it)
            })
        )
    }
}