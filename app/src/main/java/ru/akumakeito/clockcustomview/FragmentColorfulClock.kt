package ru.akumakeito.clockcustomview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentColorfulClock : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.colorful_clock_fragment, container, false)
        view.findViewById<ClockView>(R.id.clock_view).clockFaceBackgroundColor = 0x33ff0000

        return view


    }
}