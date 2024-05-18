package com.example.cypher_vault.model.encrypt

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class EncryptionService {

    fun encrypt(key: SecretKey, data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = IvParameterSpec(ByteArray(16)) // Necesitamos un vector de inicialización para el modo CBC
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        return cipher.doFinal(data)

    }

    fun decrypt(key: SecretKey, data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = IvParameterSpec(ByteArray(16)) // El mismo vector de inicialización debe ser usado para la desencriptación
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        return cipher.doFinal(data)    }

    fun generateKeyForUser(userId: String) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        keyStore.load(null)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                userId, // Usa el ID del usuario como el alias de la clave
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        )
        keyGenerator.generateKey()
    }
    fun getKeyForUser(userId: String): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val key = keyStore.getKey(userId, null) as SecretKey
        Log.d("Key", key.toHexString())
        return key
    }

    private fun SecretKey.toHexString(): String {
        return this.encoded.joinToString("") { "%02x".format(it) }
    }

}