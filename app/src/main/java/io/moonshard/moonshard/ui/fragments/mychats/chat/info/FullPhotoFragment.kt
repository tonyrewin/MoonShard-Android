package io.moonshard.moonshard.ui.fragments.mychats.chat.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import io.moonshard.moonshard.R
import kotlinx.android.synthetic.main.fragment_full_photo.*


class FullPhotoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val url = it.getString("url")

            Picasso.get().load(url)
                .into(fullImage)
        }

        close?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }
}
