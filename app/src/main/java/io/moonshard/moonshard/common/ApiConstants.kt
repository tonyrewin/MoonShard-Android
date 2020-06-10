package io.moonshard.moonshard.common

interface ApiConstants {
    companion object {
        const val COMPLEX_BASE_URL = "https://sonis.moonshard.tech"
        const val COMPLEX_BASE_URL_AUTH = "https://auth.moonshard.tech/api/v1"
        const val COMPLEX_BASE_URL_UNIT_PAY = "https://ph.moonshard.tech/api/v1"


        //auth
        const val REGISTER_URL = "/register"
        const val LOGIN_URL = "/login"
        const val RECOVERY_PASSWORD_URL = "/resetPassword"

        const val LOGOUT_URL = "/logout"
        const val REFRESH_URL = "/rotateTokens"

        //profile
        const val SAVE_PRIVATE_KEY_URL = "/profile/saveWalletKeys"
        const val GET_PRIVATE_KEY_URL = "/profile/getWalletKeys"
        const val GET_PUBLIC_KEY_URL = "/wallets/publicKey"
        const val ADD_EMAIL_TO_PROFILE_URL = "/profile/email/add"
        const val USER_PROFILE_INFO_URL = "/profile/info"


        //unitpay
        const val UNIT_PAY_CREATE_PAYMENT = "createPayment"




    }
}