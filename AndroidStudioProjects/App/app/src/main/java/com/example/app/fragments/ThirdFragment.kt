package com.example.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.app.R

class ThirdFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        val button = view.findViewById<Button>(R.id.thirdButton)
        button.setOnClickListener {
            // Реакция на нажатие третьей кнопки
        }
        return view
    }
}