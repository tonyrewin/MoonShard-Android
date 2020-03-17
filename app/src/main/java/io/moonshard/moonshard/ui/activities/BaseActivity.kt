package io.moonshard.moonshard.ui.activities

import android.util.Log
import io.moonshard.moonshard.MainApplication
import moxy.MvpAppCompatActivity
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChat


abstract class BaseActivity : MvpAppCompatActivity() {
    private var TAG: String? = null

    override fun onResume() {
        super.onResume()
        MainApplication.setLoginActivity(this)
        TAG = this.javaClass.name
    }

    override fun onPause() {
        clearReferences()
        super.onPause()
    }

    override fun onDestroy() {
        clearReferences()
        super.onDestroy()
    }

    private fun clearReferences() {
        val currActivity = MainApplication.getLoginActivity()
        if (this == currActivity) {
            MainApplication.setLoginActivity(null)
        }
    }

    open fun onAuthenticated() {

    }

    open fun onError(e: Exception) {}

    open fun onSuccess(){}

    open fun onInvitationReceivedForMuc(
        room: MultiUserChat, inviter: String,
        reason: String, password: String, message: Message
    ) {
        Log.i(TAG, "invitations received for group: " + room.room + " by : " + inviter)
    }

    fun isShowNotification(groupId: String, userId: String): Boolean {
        return true
    }
}