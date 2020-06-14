package io.moonshard.moonshard.presentation.presenter.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import com.example.moonshardwallet.MainService
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.view.profile.ProfileView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import retrofit2.HttpException
import java.math.BigInteger


@InjectViewState
class ProfilePresenter : MvpPresenter<ProfileView>() {

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = AuthUseCase()
    }

    fun getInfoProfile() {
        getInfoFromVCard().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setData(it["nickName"], it["description"], it["jidPart"])
            }, {
                it.message?.let { it1 -> viewState?.showError(it1) }
            })
    }

    private fun getInfoFromVCard(): Single<HashMap<String, String>> {
        return Single.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = card.nickName
            // val description = card.getField("DESCRIPTION")
            var description = card.middleName

            if (description.isNullOrBlank()) {
                setDescriptionInVCard(vm, card)
                description = card.middleName
            }

            val hashMapData = hashMapOf<String, String>()
            hashMapData["nickName"] = nickName
            hashMapData["description"] = description
            hashMapData["jidPart"] = MainApplication.getCurrentLoginCredentials().username
            it.onSuccess(hashMapData)
        }
    }

    fun setDescriptionInVCard(vm: VCardManager, card: VCard) {
        card.middleName = "Привет! Теперь я пользуюсь Moonshard."
        vm.saveVCard(card)
    }

    fun getAvatar() {
        getAvatarFromVCard()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setAvatar(it)
            }, {
                it.message?.let { it1 -> viewState?.showError(it1) }
            })
    }

    private fun getAvatarFromVCard(): Single<Bitmap> {
        return Single.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val avatarBytes = card.avatar

            if (avatarBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
                it.onSuccess(bitmap)
            }
        }
    }

    fun getVerificationEmail() {
        val accessToken = MainApplication.getCurrentLoginCredentials().accessToken

        compositeDisposable.add(useCase!!.getUserProfileInfo(accessToken!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                Log.d("responseUserProfileInfo", Gson().toJson(result))
                if (throwable == null) {
                    //todo получить
                    if (result.isActivated!!) getPrivateKey()
                    viewState?.setVerification(result.email, result.isActivated)
                    Logger.d(result)
                } else {
                    val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                    val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                    viewState?.showError(myError.error.message)
                }
            })
    }

    fun getPrivateKey() {
        val accessToken = getLongStringValue("accessToken")

        compositeDisposable.add(useCase!!.getPrivateKey(
            accessToken!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    if (result.privateKey.isNotBlank()) {
                        MainApplication.initWalletLibrary(result.privateKey)
                    }
                } else {
                    val jsonError = (throwable as HttpException).response()!!.errorBody()!!.string()
                    val (error) = Gson().fromJson(jsonError, ErrorResponse::class.java)

                    if (error.message == "cipher text too short") {
                        MainApplication.initWalletLibrary(null)
                    }
                    Logger.d(result)
                }
            })
    }

    fun getPublicKey(username: String) {
        val accessToken = getLongStringValue("accessToken")

        compositeDisposable.add(useCase!!.getWalletAddress(
            username, accessToken!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    Logger.d(result)
                } else {
                    Logger.d(result)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

