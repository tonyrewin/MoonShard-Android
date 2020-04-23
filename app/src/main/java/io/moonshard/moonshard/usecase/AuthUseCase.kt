package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.auth.response.GeneralResponseAuth
import io.moonshard.moonshard.models.api.auth.response.PrivateKeyAuthResponse
import io.moonshard.moonshard.models.api.auth.response.TokenModelResponse
import io.moonshard.moonshard.repository.AuthRepository
import io.reactivex.Single
import okhttp3.Response
import javax.inject.Inject

class AuthUseCase {

    @Inject
    internal lateinit var authRepository: AuthRepository

    init {
        MainApplication.getComponent().inject(this)
    }

    fun resetPassword(email:String,newPassword:String): Single<Response> {
        return authRepository.resetPassword(email,newPassword)
    }

    fun register(email:String,newPassword:String,userName:String): Single<GeneralResponseAuth> {
        return authRepository.register(email,newPassword,userName)
    }

    fun login(email:String,newPassword:String): Single<TokenModelResponse> {
        return authRepository.login(email,newPassword)
    }

    fun logout(accessToken:String,refreshToken:String): Single<GeneralResponseAuth> {
        return authRepository.logout(accessToken,refreshToken)
    }

    fun refreshToken(accessToken:String,refreshToken:String): Single<TokenModelResponse> {
        return authRepository.refreshToken(accessToken,refreshToken)
    }

    fun savePrivateKey(encryptionPassword:String,token:String): Single<PrivateKeyAuthResponse>{
        return authRepository.savePrivateKey(encryptionPassword,token)
    }
}