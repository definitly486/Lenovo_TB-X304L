package com.example.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.app.R

class ThirdFragment : Fragment() {

    var apkHttpUrl = "https://github.com/xinitronix/gnucash/raw/refs/heads/main/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        val button = view.findViewById<Button>(R.id.button5)
        button.setOnClickListener {
            val helper = DownloadHelper(requireContext())
            helper.download("${apkHttpUrl}definitly.gnucash.gpg")
        }
        return view
    }
}