package com.example.a304

import android.Manifest

import android.app.DownloadManager
import androidx.core.content.FileProvider
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.File



const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1001
val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var downloadManager: DownloadManager
    var mydownloadID: Long = 0
    var apkHttpUrl = "https://github.com/definitly486/Lenovo_TB-X304L/releases/download/apk/"

    companion object {
        const val WRITE_EXTERNAL_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkWritePermission()
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    /**
     * Проверяем разрешение на запись во внешние хранилища
     */
    private fun checkWritePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_PERMISSION_REQUEST_CODE
            )
        }
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_EXTERNAL_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "Разрешение получено!")
                } else {
                    Log.e(TAG, "Разрешение не дано.")
                }
            }
        }
    }

    /**
     * Скачать файл
     *
     * @param url Полный URL файла
     */
    fun download(url: String) {
        val folder = File(Environment.getExternalStorageDirectory(), "Download/")
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val lastPart = url.split("/").last()
        val file = File(folder, lastPart)

        if (file.exists()) {
            Toast.makeText(this, "Файл уже существует", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(IO).launch {
            try {
                withContext(Main) {
                    Toast.makeText(this@MainActivity, "Начинается загрузка...", Toast.LENGTH_SHORT).show()
                }

                val fileName = url.substringAfterLast('/')
                val request = DownloadManager.Request(Uri.parse(url))
                request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or
                            DownloadManager.Request.NETWORK_MOBILE
                )
                request.setTitle(fileName)
                request.setDescription("Загружается...")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.allowScanningByMediaScanner()
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
                )

                mydownloadID = downloadManager.enqueue(request)
            } catch (ex: Exception) {
                ex.printStackTrace()
                withContext(Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Ошибка при загрузке: ${ex.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Установить загруженный APK файл
     *
     * @param filename Название файла .apk
     */
    fun installApk(filename: String) {
        val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download/$filename"
        val file = File(filePath)

        if (file.exists()) {
            val apkUri: Uri = FileProvider.getUriForFile(
                this, // Use 'this' for Activity context
                "${applicationContext.packageName}.fileprovider", // Use 'applicationContext.packageName'
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show()
        }
    }
    fun downloadozonjob(view: View) {
        download("${apkHttpUrl}Ozon+Job_1.62.0-GMS-release_apkcombo.com.xapk")

    }

    fun downloadApp(view: View) {
        download("${apkHttpUrl}app_mpv-default-arm64-v8a-release.apk")
        download("${apkHttpUrl}ByeByeDPI-arm64-v8a-release.apk")
        download("${apkHttpUrl}File+Manager++-Premium-v3.5.4_2103054-Mod.apk")
        download("${apkHttpUrl}vcore.apk")
        download("${apkHttpUrl}com.aurora.store_70.apk")
        download("${apkHttpUrl}NewPipe_nightly-1066.apk")
    }

    fun installApp(view: View) {
        installApk("vcore.apk")
        installApk("NewPipe_nightly-1066.apk")
        installApk("File+Manager++-Premium-v3.5.4_2103054-Mod.apk")
        installApk("app_mpv-default-arm64-v8a-release.apk")
        installApk("ByeByeDPI-arm64-v8a-release.apk")
    }
}


