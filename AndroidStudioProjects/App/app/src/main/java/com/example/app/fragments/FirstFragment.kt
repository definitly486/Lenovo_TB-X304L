package com.example.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.app.R
import android.util.Log
class FirstFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        val button = view.findViewById<Button>(R.id.firstButton)
        button.setOnClickListener {
               Log.d("DEBUG_TAG", "Приложение запущено")
        }
        return view
    }
}