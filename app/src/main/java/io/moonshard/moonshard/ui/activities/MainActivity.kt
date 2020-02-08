package io.moonshard.moonshard.ui.activities

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.ui.fragments.map.MapFragment
import io.moonshard.moonshard.ui.fragments.mychats.MyChatsFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
import io.moonshard.moonshard.ui.fragments.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        methodRequiresTwoPermission()
        mainBottomNav?.selectedItemId = R.id.find_chats_map_bottom_nav_item
        MainApplication.setMainActivity(this)

        mainBottomNav?.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.my_chats_bottom_nav_item -> {
                    showMyChatsFragment()
                }
                R.id.find_chats_map_bottom_nav_item -> {
                    methodRequiresTwoPermission()
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
        showMapScreen()
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
        mainBottomNav?.menu?.getItem(1)?.isChecked = true
    }

    override fun onBackPressed() {

        if (supportFragmentManager.findFragmentByTag("CreatedChatScreen") != null) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            return
        }

        if (supportFragmentManager.findFragmentByTag("AddChatFragment") != null) {
            supportFragmentManager.popBackStack()
            ChooseChatRepository.clean()
            return
        }

        if (supportFragmentManager.findFragmentByTag("chatScreen") != null) {
            val mainChatScreen = supportFragmentManager.findFragmentByTag("chatScreen")
            if (mainChatScreen!!.childFragmentManager.backStackEntryCount > 0) {
                mainChatScreen.childFragmentManager.popBackStack()
                return
            }
        }
        supportFragmentManager.popBackStack()
    }

    private fun showMapScreen() {
        val newFragment = MapFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, newFragment, "MapScreen").commit()
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

    fun showMainChatScreen(chatId: String, fromMap: Boolean = false, stateChat: String = "join") {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        bundle.putBoolean("fromMap", fromMap)
        bundle.putString("stateChat", stateChat)
        val mainChatFragment = MainChatFragment()
        mainChatFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, mainChatFragment, "chatScreen").addToBackStack("chatScreen")
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        MainApplication.setMainActivity(null)
        // MainApplication.getXmppConnection().setStatus(false, "OFFLINE")
    }
}
