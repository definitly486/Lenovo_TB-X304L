package com.example.a304

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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
     * Проверка и запрос разрешения на запись и чтение внешних данных
     */
    private fun checkWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Новое хранилище данных для Android 11 и выше
            return
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
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
                if ((grantResults.isNotEmpty() &&
                            grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                ) {
                    Log.d(TAG, "Разрешения на запись и чтение предоставлено!")
                } else {
                    Log.e(TAG, "Не хватает разрешения на запись и чтение.")
                }
            }
        }
    }

    /**
     * Метод для начала загрузки файла
     */
    fun download(url: String) {
        val folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: return
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
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Начинается загрузка...", Toast.LENGTH_SHORT)
                        .show()
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
                request.setDestinationInExternalFilesDir(
                    this@MainActivity,
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
                )

                mydownloadID = downloadManager.enqueue(request)
            } catch (ex: Exception) {
                ex.printStackTrace()
                withContext(Dispatchers.Main) {
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
     * Устанавливает APK-файлы
     */
    fun installApk(filename: String) {
        val apkFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)

        if (apkFile.exists()) {
            val apkUri = FileProvider.getUriForFile(
                applicationContext,
                "$packageName.fileprovider",
                apkFile
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

    /**
     * Распаковывает ZIP-файл
     */
    fun unzip(filename: String): Boolean {
        val zipFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
        val outputFolder = File(getExternalFilesDir(null)?.path!!, "Download")
        outputFolder.mkdirs()
        val folder ="binance.base.apk"
        val lastPart = filename.split("/").last()
        val file = File(folder, lastPart)

        if (file.exists()) {
            Toast.makeText(this, "Файл уже существует", Toast.LENGTH_SHORT).show()
            installApk("binance.base.apk")
        }


        val fis = FileInputStream(zipFile)
        val zis = ZipInputStream(fis)

        var entry: ZipEntry?
        while (zis.nextEntry.also { entry = it } != null) {
            val fileName = entry!!.name
            val destFile = File(outputFolder, fileName)
            destFile.parentFile.mkdirs()

            if (!entry.isDirectory) {
                val fos = FileOutputStream(destFile)
                val bufferSize = 4096
                val data = ByteArray(bufferSize)
                var count: Int
                while (zis.read(data, 0, bufferSize).also { count = it } != -1) {
                    fos.write(data, 0, count)
                }
                fos.flush()
                fos.close()
            }
            zis.closeEntry()
        }
        zis.close()
        fis.close()
        return true
    }


    /**
     * Другие методы для загрузки остальных файлов
     */
    fun downloadozonjob(view: View) {
        download("${apkHttpUrl}Ozon+Job_1.62.0-GMS-release_apkcombo.com_antisplit.apk")
    }

    fun downloadntrichromelibrary(view: View) {
        download("${apkHttpUrl}com.google.android.trichromelibrary_141.0.7390.43-739004331_minAPI29_apkmirror.com.apk")
    }

    fun downloadsber(view: View) {
        download("${apkHttpUrl}SberbankOnline.apk")
    }

    fun downloadApp(view: View) {
        download("${apkHttpUrl}app_mpv-default-arm64-v8a-release.apk")
        download("${apkHttpUrl}ByeByeDPI-arm64-v8a-release.apk")
        download("${apkHttpUrl}File+Manager++-Premium-v3.5.4_2103054-Mod.apk")
        download("${apkHttpUrl}vcore.apk")
        download("${apkHttpUrl}com.aurora.store_70.apk")
        download("${apkHttpUrl}NewPipe_nightly-1066.apk")
        download("${apkHttpUrl}org-gnucash-android-24003-39426726-deeea690953a751a05a1a35017540c33.apk")
        download("${apkHttpUrl}com.android.chrome_141.0.7390.43-739004331_1lang_2feat_e182f1b6bd7ad7d24534108e4e98f9ed_apkmirror.com.apkm")
        download("${apkHttpUrl}sports+2024_1.2_apkcombo.com_antisplit.apk")
    }

    fun installApp(view: View) {
        installApk("vcore.apk")
        installApk("NewPipe_nightly-1066.apk")
        installApk("File+Manager++-Premium-v3.5.4_2103054-Mod.apk")
        installApk("app_mpv-default-arm64-v8a-release.apk")
        installApk("ByeByeDPI-arm64-v8a-release.apk")
    }

    // Приемник уведомлений о завершении загрузки
    private val downloadCompleteBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == mydownloadID) {
                Toast.makeText(applicationContext, "Загрузка завершена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        registerReceiver(
            downloadCompleteBroadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onPause() {
        unregisterReceiver(downloadCompleteBroadcastReceiver)
        super.onPause()
    }

    fun downloadbinance(view: View) {
        download("${apkHttpUrl}binance.base.zip")

    }


    fun installbinance(view: View) {
        unzip("binance.base.zip")
        installApk("binance.base.apk")
    }

}