package com.example.app.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.app.R

class SecondFragment : Fragment() {

    var apkHttpUrl = "https://github.com/definitly486/Lenovo_TB-X304L/releases/download/apk/"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        val button = view.findViewById<Button>(R.id.button5)
        button.setOnClickListener {
            val helper = DownloadHelper(requireContext())
            helper.download("${apkHttpUrl}app_mpv-default-arm64-v8a-release.apk")
            helper.download("${apkHttpUrl}ByeByeDPI-arm64-v8a-release.apk")
            helper.download("${apkHttpUrl}File+Manager++-Premium-v3.5.4_2103054-Mod.apk")
            helper.download("${apkHttpUrl}vcore.apk")
            helper.download("${apkHttpUrl}com.aurora.store_70.apk")
            helper.download("${apkHttpUrl}NewPipe_nightly-1066.apk")
            helper.download("${apkHttpUrl}org-gnucash-android-24003-39426726-deeea690953a751a05a1a35017540c33.apk")
            helper.download("${apkHttpUrl}com.android.chrome_141.0.7390.43-739004331_1lang_2feat_e182f1b6bd7ad7d24534108e4e98f9ed_apkmirror.com.apkm")
            helper.download("${apkHttpUrl}sports+2024_1.2_apkcombo.com_antisplit.apk")
            helper.download("${apkHttpUrl}Hacker_v1.41.1.apk")
            helper.download("${apkHttpUrl}terminal.apk")
        }
        val button6 = view.findViewById<Button>(R.id.button6)
        button6.setOnClickListener {
            val helper = DownloadHelper(requireContext())
            helper.installApk("vcore.apk")
            helper.installApk("NewPipe_nightly-1066.apk")
            helper.installApk("File+Manager++-Premium-v3.5.4_2103054-Mod.apk")
            helper.installApk("app_mpv-default-arm64-v8a-release.apk")
            helper.installApk("ByeByeDPI-arm64-v8a-release.apk")
        }

        val button7 = view.findViewById<Button>(R.id.button7)
        button7.setOnClickListener {
            val helper = DownloadHelper(requireContext())
            helper.download("${apkHttpUrl}binance.base.zip")
        }

        val button2 = view.findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            val helper = DownloadHelper(requireContext())
            helper.download("${apkHttpUrl}SberbankOnline.apk")
        }

        val button4 = view.findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            val helper = DownloadHelper(requireContext())
            helper.download("${apkHttpUrl}Ozon+Job_1.62.0-GMS-release_apkcombo.com_antisplit.apk")
        }

        return view
    }
}