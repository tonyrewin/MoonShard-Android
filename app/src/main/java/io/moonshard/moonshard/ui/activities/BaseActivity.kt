package io.moonshard.moonshard.ui.activities

import android.util.Log
import io.moonshard.moonshard.MainApplication
import moxy.MvpAppCompatActivity
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChat


abstract class BaseActivity : MvpAppCompatActivity() {
    private var TAG: String? = null

    override fun onResume() {
        super.onResume()
        MainApplication.setCurrentActivity(this)
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
        val currActivity = MainApplication.getCurrentActivity()
        if (this == currActivity) {
            MainApplication.setCurrentActivity(null)
        }
    }

    open fun onAuthenticated() {

    }

    open fun onError(e: Exception) {}

    fun onInvitationReceivedForMuc(
        room: MultiUserChat, inviter: String,
        reason: String, password: String, message: Message
    ) {
        Log.i(TAG, "invitations received for group: " + room.room + " by : " + inviter)
    }

    fun isShowNotification(groupId: String, userId: String): Boolean {
        return true
    }
}