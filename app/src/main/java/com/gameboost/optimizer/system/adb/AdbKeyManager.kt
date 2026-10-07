package com.gameboost.optimizer.system.adb

import android.content.Context
import android.util.Base64
import android.util.Log
import java.io.File
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.Date
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Manages RSA-2048 keypair generation and TLS SSLContext for local
 * Android Wireless Debugging connections. Keys are stored in private app storage.
 */
class AdbKeyManager(private val context: Context) {

    companion object {
        private const val TAG = "AdbKeyManager"
        private const val KEY_DIR = "adb_security"
        private const val KEYSTORE_ALIAS = "gameboost_adb"
        private const val KEYSTORE_PASS = "gameboost_keystore_pass"
    }

    private val securityDir = File(context.filesDir, KEY_DIR).apply { if (!exists()) mkdirs() }
    private val keyStoreFile = File(securityDir, "adb_credentials.bks")

    private var cachedSslContext: SSLContext? = null
    private var cachedKeyPair: KeyPair? = null

    @Synchronized
    fun getOrCreateKeyPair(): KeyPair {
        cachedKeyPair?.let { return it }

        val ks = KeyStore.getInstance(KeyStore.getDefaultType())
        val pass = KEYSTORE_PASS.toCharArray()

        if (keyStoreFile.exists()) {
            try {
                keyStoreFile.inputStream().use { ks.load(it, pass) }
                if (ks.containsAlias(KEYSTORE_ALIAS)) {
                    val privKey = ks.getKey(KEYSTORE_ALIAS, pass) as? java.security.PrivateKey
                    val cert = ks.getCertificate(KEYSTORE_ALIAS)
                    if (privKey != null && cert != null) {
                        val kp = KeyPair(cert.publicKey, privKey)
                        cachedKeyPair = kp
                        return kp
                    }
                }
            } catch (e: Throwable) {
                Log.w(TAG, "Failed to load existing keystore, regenerating: ${e.message}")
            }
        }

        // Generate fresh RSA 2048-bit keypair
        Log.i(TAG, "Generating new RSA-2048 keypair for Wireless ADB")
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048, SecureRandom())
        val newKp = kpg.generateKeyPair()

        try {
            ks.load(null, pass)
            val cert = createSelfSignedCertificate(newKp)
            ks.setKeyEntry(KEYSTORE_ALIAS, newKp.private, pass, arrayOf(cert))
            keyStoreFile.outputStream().use { ks.store(it, pass) }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed storing ADB keystore", e)
        }

        cachedKeyPair = newKp
        return newKp
    }

    @Synchronized
    fun getSslContext(): SSLContext {
        cachedSslContext?.let { return it }

        val keyPair = getOrCreateKeyPair()
        val cert = createSelfSignedCertificate(keyPair)

        val ks = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setKeyEntry("adb_client", keyPair.private, "temp_pass".toCharArray(), arrayOf(cert))
        }

        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(ks, "temp_pass".toCharArray())

        // Trust-all trust manager for 127.0.0.1 adbd peer TLS verification
        val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(kmf.keyManagers, trustAll, SecureRandom())
        cachedSslContext = sslContext
        return sslContext
    }

    /**
     * Generates a self-signed X.509 certificate for TLS client auth.
     */
    private fun createSelfSignedCertificate(keyPair: KeyPair): X509Certificate {
        // Minimal ASN.1 self-signed DER certificate or Java dummy wrapper
        // Android's adbd in pairing mode extracts the RSA public key directly
        return DummyX509Certificate(keyPair.public)
    }

    fun getPublicKeyAdbFormat(): String {
        val kp = getOrCreateKeyPair()
        val encoded = Base64.encodeToString(kp.public.encoded, Base64.NO_WRAP)
        return "$encoded gameboost@localhost"
    }

    /**
     * Lightweight X509Certificate wrapper to provide RSA public key during TLS handshake
     */
    private class DummyX509Certificate(private val pubKey: java.security.PublicKey) : X509Certificate() {
        override fun getPublicKey(): java.security.PublicKey = pubKey
        override fun getEncoded(): ByteArray = pubKey.encoded
        override fun verify(key: java.security.PublicKey?) {}
        override fun verify(key: java.security.PublicKey?, sigProvider: String?) {}
        override fun toString(): String = "AdbClientCertificate[pubKey=$pubKey]"
        override fun hasUnsupportedCriticalExtension(): Boolean = false
        override fun getCriticalExtensionOIDs(): MutableSet<String>? = null
        override fun getNonCriticalExtensionOIDs(): MutableSet<String>? = null
        override fun getExtensionValue(oid: String?): ByteArray? = null
        override fun checkValidity() {}
        override fun checkValidity(date: Date?) {}
        override fun getVersion(): Int = 3
        override fun getSerialNumber(): BigInteger = BigInteger.ONE
        override fun getIssuerDN(): java.security.Principal = java.security.Principal { "CN=GameBoost" }
        override fun getSubjectDN(): java.security.Principal = java.security.Principal { "CN=GameBoost" }
        override fun getNotBefore(): Date = Date(System.currentTimeMillis() - 86400000L)
        override fun getNotAfter(): Date = Date(System.currentTimeMillis() + 315360000000L)
        override fun getTBSCertificate(): ByteArray = ByteArray(0)
        override fun getSignature(): ByteArray = ByteArray(0)
        override fun getSigAlgName(): String = "SHA256withRSA"
        override fun getSigAlgOID(): String = "1.2.840.113549.1.1.11"
        override fun getSigAlgParams(): ByteArray? = null
        override fun getIssuerUniqueID(): BooleanArray? = null
        override fun getSubjectUniqueID(): BooleanArray? = null
        override fun getKeyUsage(): BooleanArray? = null
        override fun getBasicConstraints(): Int = -1
    }
}
