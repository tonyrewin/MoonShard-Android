package io.moonshard.moonshard.ui.activities

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.fragments.ChatsFragment
import io.moonshard.moonshard.ui.fragments.MapFragment
import io.moonshard.moonshard.ui.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newFragment = ChatsFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.container, newFragment).commit()

        mainBottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.my_chats_bottom_nav_item -> {
                    val fragment = ChatsFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.container, fragment).commit()
                }
                R.id.find_chats_map_bottom_nav_item -> {
                    requestPermissions()
                }
                R.id.settings_bottom_nav_item -> {
                    val fragment = SettingsFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.container, fragment).commit()
                }
            }
            true
        }
    }

    private fun requestPermissions() {
        val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        if (EasyPermissions.hasPermissions(this, coarseLocation, fineLocation)) {
            // Already have permission, show map screen
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

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        showMapScreen()
    }

    private fun showMapScreen() {
        val newFragment = MapFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, newFragment).commit()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.findFragmentByTag("chatScreen")!=null){
           supportFragmentManager.popBackStack()
        }
    }
}
