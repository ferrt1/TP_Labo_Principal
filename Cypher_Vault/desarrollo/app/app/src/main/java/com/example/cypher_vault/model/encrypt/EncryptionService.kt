package com.example.cypher_vault.model.encrypt

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class EncryptionService {

    private val secureRandom = SecureRandom()

    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        return salt
    }

    private fun deriveKey(password: String, salt: ByteArray, iterations: Int = 65536, keyLength: Int = 256): SecretKeySpec {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = keyFactory.generateSecret(keySpec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(password: String, data: ByteArray, salt: ByteArray): ByteArray {
        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16).also { secureRandom.nextBytes(it) }
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        val encryptedData = cipher.doFinal(data)
        return iv + encryptedData
    }

    fun decrypt(password: String, data: ByteArray, salt: ByteArray): ByteArray {
        val iv = data.copyOfRange(0, 16)
        val encryptedData = data.copyOfRange(16, data.size)
        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        return cipher.doFinal(encryptedData)
    }
}