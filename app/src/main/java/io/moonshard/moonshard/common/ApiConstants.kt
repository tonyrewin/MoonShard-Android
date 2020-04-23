package io.moonshard.moonshard.common

interface ApiConstants {
    companion object {
        const val COMPLEX_BASE_URL = "https://sonis.moonshard.tech"
        const val COMPLEX_BASE_URL_AUTH = "https://auth.moonshard.tech"


        //auth
        const val REGISTER_URL = "/register"
        const val LOGIN_URL = "/login"
        const val RECOVERY_PASSWORD_URL = "/resetPassword"

        const val LOGOUT_URL = "/logout"
        const val REFRESH_URL = "/rotateTokens"
        const val SAVE_PRIVATE_KEY_URL = "/profile/savePrivateKey"

    }
}