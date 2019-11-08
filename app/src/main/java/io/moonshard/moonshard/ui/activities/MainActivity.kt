package io.moonshard.moonshard.ui.activities

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenuItemView

import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.fragments.ChatsFragment
import io.moonshard.moonshard.ui.fragments.MapFragment
import io.moonshard.moonshard.ui.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
                    R.id.myChats ->{
                        val newFragment = ChatsFragment()
                        val ft = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.container, newFragment).commit()
                    }
                    R.id.findChatsMap -> {
                        val newFragment = MapFragment()
                        val ft = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.container, newFragment).commit()
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
}
