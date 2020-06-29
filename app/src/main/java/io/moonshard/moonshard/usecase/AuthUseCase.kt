package io.moonshard.moonshard.usecase

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.auth.response.*
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

    fun resetPassword(email:String,newPassword:String): Single<GeneralResponseAuth> {
        return authRepository.resetPassword(email,newPassword)
    }

    fun register(username:String,password:String): Single<GeneralResponseAuth> {
        return authRepository.register(username,password)
    }

    fun login(email:String,newPassword:String): Single<TokenModelResponse> {
        return authRepository.login(email,newPassword)
    }

    fun logout(accessToken:String,refreshToken:String): Single<GeneralResponseAuth> {
        return authRepository.logout(accessToken,refreshToken)
    }

    fun addEmailToProfile(accessToken: String,email: String):Single<GeneralResponseAuth>{
        return authRepository.addEmailToProfile(accessToken,email)
    }

    fun refreshToken(accessToken:String,refreshToken:String): Single<TokenModelResponse> {
        return authRepository.refreshToken(accessToken,refreshToken)
    }

    fun savePrivateKey(privateKey:String,addressWallet:String,token:String): Single<GeneralResponseAuth>{
        return authRepository.savePrivateKey(privateKey,addressWallet,token)
    }

    fun getPrivateKey(token:String): Single<PrivateKeyAuthResponse>{
        return authRepository.getPrivateKey(token)
    }

    fun getWalletAddress(encryptionPassword:String,token:String): Single<PublicKeyAuthResponse>{
        return authRepository.getWalletAddress(encryptionPassword,token)
    }

    fun getUserProfileInfo(token:String): Single<ProfileUserResponse>{
        return authRepository.getUserProfileInfo(token)
    }
}