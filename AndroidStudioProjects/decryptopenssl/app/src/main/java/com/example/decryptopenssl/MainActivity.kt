package com.example.decryptopenssl

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.decryptopenssl.databinding.ActivityMainBinding
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@Suppress("SameParameterValue")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var inputFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Security.addProvider(BouncyCastleProvider())

        // Обработчик выбора файла
        val selectFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { updateSelectedFileInfo(it) }
        }

        binding.buttonSelectFile.setOnClickListener {
            if (isStoragePermissionGranted()) {
                selectFileLauncher.launch(arrayOf("*/*"))
            } else {
                requestStoragePermission()
            }
        }

        // Обработчик расшифровки
        binding.buttonDecrypt.setOnClickListener {
            val password = binding.passwordInput.text.toString()
            if (password.isEmpty() || inputFileUri == null) {
                Toast.makeText(this, "Заполните пароль и выберите файл", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            decryptFile(inputFileUri!!, password)
        }
    }

    // Функция обновления информации о выбранном файле
    @SuppressLint("SetTextI18n")
    private fun updateSelectedFileInfo(uri: Uri) {
        inputFileUri = uri
        binding.fileNameLabel.text = "Выбран файл: $uri"
    }

    // Проверка разрешений на доступ к хранилищу
    private fun isStoragePermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED

    // Запрашиваем разрешение на доступ к хранилищу
    private fun requestStoragePermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
    }

    // Обрабатываем результат запрашиваемых разрешений
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.firstOrNull() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            binding.buttonSelectFile.callOnClick()
        } else {
            Toast.makeText(this, "Доступ запрещен", Toast.LENGTH_LONG).show()
        }
    }

    // Основной метод расшифровки файла
    @SuppressLint("SetTextI18n")
    private fun decryptFile(fileUri: Uri, password: String) {
        Thread {
            try {
                // Чтение содержимого файла
                val stream = contentResolver.openInputStream(fileUri)!!
                val rawBase64Data = InputStreamReader(stream).readText()

                // Очистка строки от посторонних символов
                val cleanedBase64 = rawBase64Data.trim().replace("[^A-Za-z0-9+/=]+".toRegex(), "")

                // Дополнение длины до кратности 4
                val paddedBase64 = cleanedBase64.padEnd((cleanedBase64.length + 3) / 4 * 4, '=')

                // Попытка декодирования
                val decodedData = Base64.decode(paddedBase64)

                // Процесс расшифровки
                val decryptedData = decryptWithOpenSSLAES(decodedData, password)

                runOnUiThread {
                    binding.outputData.text = "Результат:\n$decryptedData"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Ошибка при расшифровке: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
    // Основная логика расшифровки AES CBC
    private fun decryptWithOpenSSLAES(cipheredData: ByteArray, password: String): String {
        val keySalt = byteArrayOf(0x73, 0x6F, 0x6D, 0x65, 0x53, 0x61, 0x6C, 0x74) // Произвольный соль
        val keyIter = 100000
        val keyLen = 256

        // Генерация ключа и вектора инициализации
        val key = derivePBKDF2Key(password.toCharArray(), keySalt, keyIter, keyLen)
        val iv = ByteArray(16) // Первый блок исходных данных используется как вектор инициализации
        System.arraycopy(cipheredData, 0, iv, 0, iv.size.coerceAtMost(cipheredData.size))

        // Распаковка данных и их расшифровка
        val cipherData = ByteArrayInputStream(cipheredData)
        val plainData = decryptAES256CBC(key, iv, cipherData.readBytes())

        return String(plainData)
    }

    // Функция для получения ключа на основе пароля (PBKDF2)
    private fun derivePBKDF2Key(password: CharArray, salt: ByteArray, iterations: Int, length: Int): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, length)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1", "BC")
        return factory.generateSecret(spec).encoded
    }

    // Декодирование AES CBC
    @SuppressLint("DeprecatedProvider")
    private fun decryptAES256CBC(key: ByteArray, iv: ByteArray, ciphertext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC")
        val secretKey = SecretKeySpec(key, "AES")
        val ivParams = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams)
        return cipher.doFinal(ciphertext)
    }
}