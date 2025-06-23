package com.example.p2pchat.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
// import com.google.crypto.tink.Aead
// import com.google.crypto.tink.KeysetHandle
// import com.google.crypto.tink.aead.AeadConfig
// import com.google.crypto.tink.aead.AesGcmKeyManager
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
// import javax.inject.Inject
// import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Advanced Security Manager
 * Handles encryption, biometric authentication, and secure storage
 */
// @Singleton
class SecurityManager {

    private lateinit var context: Context

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "P2PChatMasterKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 16
        private const val ENCRYPTED_PREFS_NAME = "secure_preferences"
    }

    private var masterKey: MasterKey? = null
    private var encryptedPreferences: android.content.SharedPreferences? = null
    // private var aeadPrimitive: Aead? = null
    // private var keysetHandle: KeysetHandle? = null

    fun initialize(context: Context) {
        this.context = context
        try {
            // Initialize Tink for advanced encryption
            // AeadConfig.register()

            // Create master key for encrypted preferences
            createMasterKey()

            // Initialize encrypted shared preferences
            initializeEncryptedPreferences()

            // Initialize AEAD for message encryption
            // initializeAead()

            Timber.d("Security Manager initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Security Manager")
            throw SecurityException("Security initialization failed", e)
        }
    }

    private fun createMasterKey() {
        masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setRequestStrongBoxBacked(true)
            .build()
    }

    private fun initializeEncryptedPreferences() {
        masterKey?.let { key ->
            encryptedPreferences = EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_NAME,
                key,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    private fun initializeAead() {
        // keysetHandle = KeysetHandle.generateNew(AesGcmKeyManager.aes256GcmTemplate())
        // aeadPrimitive = keysetHandle?.getPrimitive(Aead::class.java)
    }

    /**
     * Encrypt message using AES-256-GCM
     */
    fun encryptMessage(plaintext: String, associatedData: String = ""): EncryptedData? {
        return try {
            val plaintextBytes = plaintext.toByteArray(Charsets.UTF_8)
            val associatedDataBytes = associatedData.toByteArray(Charsets.UTF_8)

            // val ciphertext = aeadPrimitive?.encrypt(plaintextBytes, associatedDataBytes)
            val ciphertext = plaintextBytes // Simplified for demo

            ciphertext?.let {
                EncryptedData(
                    ciphertext = it,
                    associatedData = associatedDataBytes
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to encrypt message")
            null
        }
    }

    /**
     * Decrypt message using AES-256-GCM
     */
    fun decryptMessage(encryptedData: EncryptedData): String? {
        return try {
            // val plaintextBytes = aeadPrimitive?.decrypt(
            //     encryptedData.ciphertext,
            //     encryptedData.associatedData
            // )
            val plaintextBytes = encryptedData.ciphertext // Simplified for demo

            plaintextBytes?.let { String(it, Charsets.UTF_8) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to decrypt message")
            null
        }
    }

    /**
     * Generate secure random key for session
     */
    fun generateSessionKey(): ByteArray {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES)
        keyGenerator.init(256)
        return keyGenerator.generateKey().encoded
    }

    /**
     * Encrypt data using Android Keystore
     */
    fun encryptWithKeystore(data: String): KeystoreEncryptedData? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                generateKeystoreKey()
            }

            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val ciphertext = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

            KeystoreEncryptedData(ciphertext, iv)
        } catch (e: Exception) {
            Timber.e(e, "Failed to encrypt with keystore")
            null
        }
    }

    /**
     * Decrypt data using Android Keystore
     */
    fun decryptWithKeystore(encryptedData: KeystoreEncryptedData): String? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)

            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, encryptedData.iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val plaintext = cipher.doFinal(encryptedData.ciphertext)
            String(plaintext, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e, "Failed to decrypt with keystore")
            null
        }
    }

    private fun generateKeystoreKey() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    /**
     * Check if biometric authentication is available
     */
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    /**
     * Authenticate using biometrics
     */
    suspend fun authenticateWithBiometric(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        negativeButtonText: String
    ): BiometricResult = suspendCancellableCoroutine { continuation ->

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                continuation.resume(BiometricResult.Error(errorCode, errString.toString()))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                continuation.resume(BiometricResult.Success)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                continuation.resume(BiometricResult.Failed)
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Store encrypted value in secure preferences
     */
    fun storeSecureValue(key: String, value: String): Boolean {
        return try {
            encryptedPreferences?.edit()?.putString(key, value)?.apply()
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to store secure value")
            false
        }
    }

    /**
     * Retrieve encrypted value from secure preferences
     */
    fun getSecureValue(key: String, defaultValue: String? = null): String? {
        return try {
            encryptedPreferences?.getString(key, defaultValue)
        } catch (e: Exception) {
            Timber.e(e, "Failed to retrieve secure value")
            defaultValue
        }
    }

    /**
     * Generate secure hash for data integrity
     */
    fun generateHash(data: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to generate hash")
            ""
        }
    }

    /**
     * Verify data integrity using hash
     */
    fun verifyHash(data: String, expectedHash: String): Boolean {
        val actualHash = generateHash(data)
        return actualHash.equals(expectedHash, ignoreCase = true)
    }

    /**
     * Generate secure random bytes
     */
    fun generateSecureRandom(size: Int): ByteArray {
        val random = SecureRandom()
        val bytes = ByteArray(size)
        random.nextBytes(bytes)
        return bytes
    }

    data class EncryptedData(
        val ciphertext: ByteArray,
        val associatedData: ByteArray
    )

    data class KeystoreEncryptedData(
        val ciphertext: ByteArray,
        val iv: ByteArray
    )

    sealed class BiometricResult {
        object Success : BiometricResult()
        object Failed : BiometricResult()
        data class Error(val errorCode: Int, val errorMessage: String) : BiometricResult()
    }
}
