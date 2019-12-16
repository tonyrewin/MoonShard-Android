package io.moonshard.moonshard.ui.fragments.onboard

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heinrichreimersoftware.materialintro.app.SlideFragment

import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.activities.onboard.MainIntroActivity
import kotlinx.android.synthetic.main.fragment_new_place.*


class NewPlaceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_place, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipBtn?.setOnClickListener {
            (activity as MainIntroActivity).nextSlide()
        }
    }

    fun newInstance(): NewPlaceFragment {
        return NewPlaceFragment()
    }
}
