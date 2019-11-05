package io.moonshard.moonshard.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback


class MapFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null


    override fun onMapReady(map: GoogleMap?) {

        mMap = map

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(io.moonshard.moonshard.R.layout.fragment_map, container, false)
    }
}
