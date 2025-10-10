package com.example.app.fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.app.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import java.io.BufferedInputStream


class ThirdFragment : Fragment() {

    var apkHttpUrl = "https://github.com/xinitronix/gnucash/raw/refs/heads/main/"
    var maitargzHttpUrl = "https://github.com/definitly486/Lenovo_TB-X304L/archive/"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        val button = view.findViewById<Button>(R.id.button5)
        button.setOnClickListener {
            val helper = DownloadHelper(requireContext())
            helper.download("${apkHttpUrl}definitly.gnucash.gpg")
        }


        val button6 = view.findViewById<Button>(R.id.button6)
        button6.setOnClickListener {
            val helper = DownloadHelper(requireContext())
            helper.download("${maitargzHttpUrl}main.tar.gz")
        }

        val button7 = view.findViewById<Button>(R.id.button7)
        button7.setOnClickListener {
            val folder = getDownloadFolder() ?: return@setOnClickListener
            val tarGzFile = File(folder,"main.tar.gz")
            val outputDir = File(folder,"")
            val helper = DownloadHelper(requireContext())
            decompressTarGz (tarGzFile, outputDir)
        }


        return view
    }

    fun getDownloadFolder(): File? {
        return context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    }


    fun decompressTarGz(tarGzFile: File, outputDir: File) {

        // Ensure canonical path for security
        outputDir.canonicalFile

        if (!tarGzFile.exists()) throw FileNotFoundException("File not found: ${tarGzFile.path}") as Throwable
        GzipCompressorInputStream(BufferedInputStream(FileInputStream(tarGzFile))).use { gzIn ->
            TarArchiveInputStream(gzIn).use { tarIn ->
                generateSequence { tarIn.nextEntry }.forEach { entry ->

                    val outputFile = File(outputDir, entry.name).canonicalFile

                    // Check if the extracted file stays inside outputDir
                    // Prevent Zip Slip Vulnerability
                    // if (!outputFile.toPath().startsWith(canonicalOutputDir.toPath())) {
                    //     throw SecurityException("Zip Slip vulnerability detected! Malicious entry: ${entry.name}")
                    //  }

                    if (entry.isDirectory) outputFile.mkdirs()
                    else {
                        outputFile.parentFile.mkdirs()
                        outputFile.outputStream().use { outStream ->
                            tarIn.copyTo(outStream)
                        }
                    }
                }
            }
        }
    }


}
