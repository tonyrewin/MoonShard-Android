package io.moonshard.moonshard.repository

import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.ApiConstants.Companion.ADD_EMAIL_TO_PROFILE_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.COMPLEX_BASE_URL_AUTH
import io.moonshard.moonshard.common.ApiConstants.Companion.GET_PRIVATE_KEY_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.GET_PUBLIC_KEY_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.LOGIN_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.LOGOUT_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.RECOVERY_PASSWORD_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.REFRESH_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.REGISTER_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.SAVE_PRIVATE_KEY_URL
import io.moonshard.moonshard.common.ApiConstants.Companion.USER_PROFILE_INFO_URL
import io.moonshard.moonshard.models.api.auth.request.*
import io.moonshard.moonshard.models.api.auth.response.*
import io.reactivex.Single
import okhttp3.Response
import javax.inject.Inject

class AuthRepository {

    @Inject
    internal lateinit var api: API

    init {
        MainApplication.getComponent().inject(this)
    }

    fun resetPassword(email:String,newPassword:String): Single<GeneralResponseAuth> {
        val modelRecoveryPass  =
            RecoveryPassRequestModel(
                email,
                newPassword
            )
        return api.resetPassword(COMPLEX_BASE_URL_AUTH+ RECOVERY_PASSWORD_URL,modelRecoveryPass)
    }

    fun login(email:String,newPassword:String): Single<TokenModelResponse> {
        val modelRecoveryPass  =
            LoginRequestModel(
                email,
                newPassword
            )
        return api.login(COMPLEX_BASE_URL_AUTH + LOGIN_URL,modelRecoveryPass)
    }

    fun register(username:String,password:String): Single<GeneralResponseAuth> {
        val model  =
            RegisterRequestModel(
                username,
                password
            )
        return api.register(COMPLEX_BASE_URL_AUTH+ REGISTER_URL,model)
    }

    fun logout(accessToken:String,refreshToken:String): Single<GeneralResponseAuth> {
        val model  =
            TokenRequestModel(
                accessToken,
                refreshToken
            )
        return api.logout(COMPLEX_BASE_URL_AUTH+ LOGOUT_URL,model)
    }

    fun addEmailToProfile(email: String,accessToken: String):Single<GeneralResponseAuth>{
        val model  =
            EmailToProfileRequestModel(email)
        return api.addEmailToProfile(COMPLEX_BASE_URL_AUTH+ ADD_EMAIL_TO_PROFILE_URL,model,
            "Bearer $accessToken"
        )
    }
    fun refreshToken(accessToken:String,refreshToken:String): Single<TokenModelResponse> {
        val model  =
            TokenRequestModel(
                accessToken,
                refreshToken
            )
        return api.refreshToken(COMPLEX_BASE_URL_AUTH+ REFRESH_URL,model)
    }

    fun savePrivateKey(privateKey:String,addressWallet:String,token:String): Single<GeneralResponseAuth> {
        val model  =
            PrivateKeyRequestModel(privateKey,addressWallet
            )
        return api.savePrivateKey(COMPLEX_BASE_URL_AUTH+ SAVE_PRIVATE_KEY_URL,model,
            "Bearer $token"
        )
    }

    fun getPrivateKey(token:String): Single<PrivateKeyAuthResponse> {
        return api.getPrivateKey(COMPLEX_BASE_URL_AUTH+ GET_PRIVATE_KEY_URL,
            "Bearer $token"
        )
    }

    fun getWalletAddress(username:String,token:String): Single<PublicKeyAuthResponse> {
        return api.getPublicKey(COMPLEX_BASE_URL_AUTH+ GET_PUBLIC_KEY_URL,username,
            "Bearer $token"
        )
    }

    fun getUserProfileInfo(token:String): Single<ProfileUserResponse> {
        return api.getUserProfileInfo(COMPLEX_BASE_URL_AUTH+ USER_PROFILE_INFO_URL,
            "Bearer $token"
        )
    }
}