package ru.akumakeito.clockcustomview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentBigClock : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.big_clock_fragment, container, false)
        view.findViewById<ClockView>(R.id.clock_view).layoutParams.apply {
            width = 1000
            height = 1000
        }

        return view

    }
}