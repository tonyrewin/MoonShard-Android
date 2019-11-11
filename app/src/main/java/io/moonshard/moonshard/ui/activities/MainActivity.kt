package io.moonshard.moonshard.ui.activities

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        initEvent()
    }

    private fun initEvent() {
        bottomBar.onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.myChats -> {
                        val newFragment = ChatsFragment()
                        val ft = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.container, newFragment).commit()
                    }
                    R.id.findChatsMap -> {
                        methodRequiresTwoPermission()
                    }
                    R.id.settings -> {
                        val newFragment = SettingsFragment()
                        val ft = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.container, newFragment).commit()
                    }
                }
                true
            }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        showMapScreen()
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

    private fun showMapScreen() {
        val newFragment = MapFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, newFragment).commit()
    }
}
