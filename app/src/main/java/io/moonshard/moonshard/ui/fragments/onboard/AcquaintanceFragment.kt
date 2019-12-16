package io.moonshard.moonshard.ui.fragments.onboard

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.paolorotolo.appintro.ISlidePolicy
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import io.moonshard.moonshard.ui.activities.onboard.MainIntroActivity
import kotlinx.android.synthetic.main.fragment_acquaintance.*


class AcquaintanceFragment : Fragment(), ISlidePolicy {
    override fun isPolicyRespected(): Boolean {
        return true
    }

    override fun onUserIllegallyRequestedNextPage() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(io.moonshard.moonshard.R.layout.fragment_acquaintance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipBtn?.setOnClickListener {
            (activity as MainIntroActivity).nextSlide()
        }
    }

    fun newInstance(): AcquaintanceFragment {
        return AcquaintanceFragment()
    }
}
