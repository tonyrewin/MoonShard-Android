package io.moonshard.moonshard.presentation.presenter.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.moonshardwallet.MainService
import com.google.gson.Gson
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.view.profile.ProfileView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
        getInfoFromVCard().
             subscribeOn(Schedulers.io())
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

            if(description.isNullOrBlank()){
                setDescriptionInVCard(vm,card)
                description = card.middleName
            }

            val hashMapData = hashMapOf<String, String>()
            hashMapData["nickName"] = nickName
            hashMapData["description"] = description
            hashMapData["jidPart"] = MainApplication.getCurrentLoginCredentials().username
            it.onSuccess(hashMapData)
        }
    }

    fun setDescriptionInVCard(vm:VCardManager,card:VCard){
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
                Log.d("responseUserProfileInfo",Gson().toJson(result))
                if (throwable == null) {
                    if(result.isActivated!!) MainApplication.initWalletLibrary()
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
        val password = SecurePreferences.getStringValue("pass", null)
        val accessToken = getLongStringValue("accessToken")
        val addressWallet = MainService.getWalletService().myAddress
        val privateKeyWallet =   MainService.getWalletService().privateKeyInHex

        compositeDisposable.add(useCase!!.savePrivateKey(
            password!!, privateKeyWallet, accessToken!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    getPrivateKey("1234567891234563")
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    fun acceptTicketAsPresent() {
        MainService.getBuyTicketService().acceptTicketAsPresentRx(
            "0xa7f81a3596000c4a661d8c0c47d6df9b9bd4f33c",
            BigInteger.valueOf(6L)
        ).thenAccept {


        }.exceptionally { e ->
            null
        }
    }
}

