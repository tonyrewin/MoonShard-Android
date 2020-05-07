package io.moonshard.moonshard.common

interface ApiConstants {
    companion object {
        const val COMPLEX_BASE_URL = "https://sonis.moonshard.tech"
        const val COMPLEX_BASE_URL_AUTH = "https://auth.moonshard.tech/api/v1"


        //auth
        const val REGISTER_URL = "/register"
        const val LOGIN_URL = "/login"
        const val RECOVERY_PASSWORD_URL = "/resetPassword"

        const val LOGOUT_URL = "/logout"
        const val REFRESH_URL = "/rotateTokens"

        //profile
        const val SAVE_PRIVATE_KEY_URL = "/profile/savePrivateKey"
        const val GET_PRIVATE_KEY_URL = "/profile/getPrivateKey"
        const val ADD_EMAIL_TO_PROFILE_URL = "/profile/email/add"
        const val USER_PROFILE_INFO_URL = "/profile/info"



    }
}