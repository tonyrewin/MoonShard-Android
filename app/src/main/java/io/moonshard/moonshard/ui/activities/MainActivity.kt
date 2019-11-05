package io.moonshard.moonshard.ui.activities

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle

import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.fragments.MapFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newFragment = MapFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.container, newFragment).commit()
    }
}
