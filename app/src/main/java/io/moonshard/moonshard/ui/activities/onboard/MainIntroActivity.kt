package io.moonshard.moonshard.ui.activities.onboard

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import io.moonshard.moonshard.ui.fragments.onboard.AcquaintanceFragment
import io.moonshard.moonshard.ui.fragments.onboard.ConnectionOnBoardFragment
import io.moonshard.moonshard.ui.fragments.onboard.NewPlaceFragment


class MainIntroActivity : AppIntro() {

    val EXTRA_FULLSCREEN = "com.heinrichreimersoftware.materialintro.demo.EXTRA_FULLSCREEN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(AcquaintanceFragment().newInstance())
        addSlide(NewPlaceFragment().newInstance())
        addSlide(ConnectionOnBoardFragment().newInstance())

        showSkipButton(false)
        isProgressButtonEnabled = false
        showSeparator(false)

        setIndicatorColor(Color.parseColor("#9B9BB6"), Color.parseColor("#DDDDDD"))
    }

    fun nextSlide(){
        pager.goToNextSlide()
    }

}
