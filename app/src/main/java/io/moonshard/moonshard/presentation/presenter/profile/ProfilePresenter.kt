package io.moonshard.moonshard.presentation.presenter.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.view.profile.ProfileView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager
import retrofit2.HttpException


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

    private fun getInfoFromVCard(): Observable<HashMap<String, String>> {
        return Observable.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = card.nickName
            // val description = card.getField("DESCRIPTION")
            val description = card.middleName
            val hashMapData = hashMapOf<String, String>()
            hashMapData["nickName"] = nickName
            hashMapData["description"] = description
            hashMapData["jidPart"] = card.to.asBareJid().localpartOrNull.toString()
            it.onNext(hashMapData)
        }
    }

    fun getAvatar() {
        getAvatarFromVCard().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setAvatar(it)
            }, {
                it.message?.let { it1 -> viewState?.showError(it1) }
            })
    }

    private fun getAvatarFromVCard(): Observable<Bitmap> {
        return Observable.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val avatarBytes = card.avatar

            if (avatarBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
                it.onNext(bitmap)
            }
        }
    }

    fun getVerificationEmail() {
        val accessToken = getLongStringValue("accessToken")

        compositeDisposable.add(useCase!!.getUserProfileInfo(accessToken!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    viewState?.setVerification(result.email, result.isActivated)
                    com.orhanobut.logger.Logger.d(result)
                } else {
                    val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                    val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                    viewState?.showError(myError.error.message)
                }
            })
    }

    fun savePrivateKey(encryptionPassword: String) {
        val privateKey = SecurePreferences.getStringValue("pass", null)
        val accessToken = getLongStringValue("accessToken")

        compositeDisposable.add(useCase!!.savePrivateKey(
            "1234567891234569", "12312testValeraMineBlaBlaBla", accessToken!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    getPrivateKey("1234567891234567")
                } else {
                    com.orhanobut.logger.Logger.d(result)
                }
            })
    }

    fun getPrivateKey(encryptionPassword: String) {
        val accessToken = getLongStringValue("accessToken")

        compositeDisposable.add(useCase!!.getPrivateKey(
            encryptionPassword, accessToken!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    com.orhanobut.logger.Logger.d(result)
                } else {
                    com.orhanobut.logger.Logger.d(result)
                }
            })
    }
}

