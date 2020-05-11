package io.moonshard.moonshard.ui.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.services.XMPPConnectionService
import io.moonshard.moonshard.ui.fragments.map.MapFragment
import io.moonshard.moonshard.ui.fragments.mychats.MyChatsFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.CreateGroupFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.chat.CreateNewChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.event.ChooseMapFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.event.CreateNewEventFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.event.TimeEventFragment
import io.moonshard.moonshard.ui.fragments.profile.ProfileFragment
import io.moonshard.moonshard.ui.fragments.profile.VerificationEmailFragment
import io.moonshard.moonshard.ui.fragments.profile.history.HistoryTransactionFragment
import io.moonshard.moonshard.ui.fragments.profile.mytickets.MyTicketInfoFragment
import io.moonshard.moonshard.ui.fragments.profile.mytickets.MyTicketsFragment
import io.moonshard.moonshard.ui.fragments.profile.present_ticket.PresentTicketFragment
import io.moonshard.moonshard.ui.fragments.profile.present_ticket.TypeTicketPresentFragment
import io.moonshard.moonshard.ui.fragments.profile.wallet.WalletFragment
import io.moonshard.moonshard.ui.fragments.profile.wallet.fill_up.FillUpWalletFragment
import io.moonshard.moonshard.ui.fragments.profile.wallet.transfer.TransferWalletFragment
import io.moonshard.moonshard.ui.fragments.profile.wallet.withdraw.WithdrawWalletFragment
import io.moonshard.moonshard.ui.fragments.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChat
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainBottomNav?.selectedItemId = R.id.find_chats_map_bottom_nav_item
        MainApplication.setMainActivity(this)

        if (intent.getStringExtra("screen") == "chat") {
            val chatId = intent.getStringExtra("chatId")
            showMyChatsFragment()
            showMainChatScreen(chatId)
        } else if (intent.getStringExtra("screen") == "my_chats") {
            showMyChatsFragment()
        } else {
            methodRequiresTwoPermission()
        }

        mainBottomNav?.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.find_chats_map_bottom_nav_item -> {
                    methodRequiresTwoPermission()
                }
                R.id.my_chats_bottom_nav_item -> {
                    showMyChatsFragment()
                }
                R.id.profile_bottom_nav_item -> {
                    showProfileFragment()
                }
                R.id.settings_bottom_nav_item -> {
                    showSettingsFragment()
                }
            }
            true
        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startService(Intent(applicationContext, XMPPConnectionService::class.java))

        android.os.Handler().postDelayed({
            showMapScreen()
        }, 500)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun methodRequiresTwoPermission() {
        val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        if (EasyPermissions.hasPermissions(this, coarseLocation, fineLocation)) {
            // Already have permission, do the thing
            showMapScreen()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your location",
                1,
                coarseLocation,
                fineLocation
            )
        }
    }

    fun showBottomNavigationBar() {
        mainBottomNav?.visibility = View.VISIBLE
    }

    fun hideBottomNavigationBar() {
        mainBottomNav?.visibility = View.GONE
    }

    fun setMapActiveBottomBar() {
        mainBottomNav?.menu?.getItem(0)?.isChecked = true
    }

    override fun onBackPressed() {

        if (intent.getStringExtra("screen") == "chat")

            if (supportFragmentManager.findFragmentByTag("CreatedChatScreen") != null) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                return
            }

        if (supportFragmentManager.findFragmentByTag("AddChatFragment") != null) {
            supportFragmentManager.popBackStack()
            ChooseChatRepository.clean()
            return
        }


        val mainChatScreen = supportFragmentManager.findFragmentByTag("chatScreen")
        if (mainChatScreen != null) {
            if (mainChatScreen.childFragmentManager.backStackEntryCount > 0) {
                mainChatScreen.childFragmentManager.popBackStack()
                return
            } else {
                supportFragmentManager.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
            return
        }
        supportFragmentManager.popBackStack()
    }

    fun removeAllFragmentsFromStack() {
        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

    fun showMapScreen() {
        val newFragment = MapFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, newFragment, "MapScreen").commit()
    }


    fun showMapScrenFromCreateNewEventScreen() {
        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        showMapScreen()
    }

    fun showMyChatsFragment() {
        val fragment = MyChatsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment).show(fragment).commit()
    }

    fun showSettingsFragment() {
        val fragment = SettingsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment).commit()
    }

    fun showProfileFragment() {
        val fragment =
            ProfileFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment).commit()
    }

    fun showMainChatScreen(
        chatId: String,
        fromMap: Boolean = false,
        stateChat: String = "join",
        fromCreateNewChat: Boolean = false
    ) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        bundle.putBoolean("fromMap", fromMap)
        bundle.putString("stateChat", stateChat)
        bundle.putBoolean("fromCreateNewChat", fromCreateNewChat)
        val mainChatFragment = MainChatFragment()
        mainChatFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, mainChatFragment, "chatScreen")
            .addToBackStack("chatScreen")
            .commit()
    }

    fun showCreateNewEventScreen(fromEventsFragment: Boolean = false) {
        val bundle = Bundle()
        bundle.putBoolean("fromEventsFragment", fromEventsFragment)
        val chatFragment =
            CreateNewEventFragment()
        chatFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, chatFragment, "CreateNewEventFragment")
            .addToBackStack("CreateNewEventFragment")
            .commit()
    }

    fun showCreateGroupScreen() {
        val newFragment = CreateGroupFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, newFragment, "CreateGroupFragment")
            .addToBackStack("CreateGroupFragment")
            .commit()
    }

    fun showCreateNewChatScreen() {
        val chatFragment =
            CreateNewChatFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, chatFragment, "CreateNewChatFragment")
            .addToBackStack("CreateNewChatFragment").commit()
    }

    fun showChooseMapScreen() {
        val chatFragment = ChooseMapFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, chatFragment, "ChooseMapFragment")
            .addToBackStack("ChooseMapFragment").commit()
    }

    fun showTimeEventScreen() {
        val chatFragment =
            TimeEventFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, chatFragment, "TimeEventFragment")
            .addToBackStack("TimeEventFragment")
            .commit()
    }

    fun showWalletFragment() {
        val fragment = WalletFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "WalletFragment").addToBackStack("WalletFragment")
            .commit()
    }

    fun showMyTicketsFragment() {
        val fragment = MyTicketsFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "MyTicketsFragment")
            .addToBackStack("MyTicketsFragment")
            .commit()
    }

    fun showFillUpWalletFragment() {
        val fragment =
            FillUpWalletFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "FillUpWalletFragment")
            .addToBackStack("FillUpWalletFragment")
            .commit()
    }

    fun showWithdrawWalletFragment() {
        val fragment =
            WithdrawWalletFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "WithdrawWalletFragment")
            .addToBackStack("WithdrawWalletFragment")
            .commit()
    }

    fun showTransferWalletFragment() {
        val fragment = TransferWalletFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "TransferWalletFragment")
            .addToBackStack("TransferWalletFragment")
            .commit()
    }

    fun showHistoryTransactionScreen() {
        val fragment = HistoryTransactionFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "HistoryTransactionFragment")
            .addToBackStack("HistoryTransactionFragment")
            .commit()
    }

    /*
    fun showTypeTicketsFragment(){
        val fragment = TypeTicketsFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "TypeTicketsFragment").addToBackStack("TypeTicketsFragment")
            .commit()
    }

     */

    fun showPresentTicketFragment() {
        val fragment = PresentTicketFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "PresentTicketFragment")
            .addToBackStack("PresentTicketFragment")
            .commit()
    }

    fun showVerificationEmailScreen() {
        val fragment = VerificationEmailFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "showVerificationEmailScreen")
            .addToBackStack("showVerificationEmailScreen")
            .commit()
    }

    fun showTypeTicketsPresentFragment() {
        val fragment = TypeTicketPresentFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "TypeTicketPresentFragment")
            .addToBackStack("TypeTicketPresentFragment")
            .commit()
    }

    fun showMyTicketInfoFragment() {
        val fragment = MyTicketInfoFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, "MyTicketInfoFragment")
            .addToBackStack("MyTicketInfoFragment")
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        MainApplication.setMainActivity(null)
    }

    override fun onInvitationReceivedForMuc(
        room: MultiUserChat,
        inviter: String,
        reason: String,
        password: String,
        message: Message
    ) {

    }
}
