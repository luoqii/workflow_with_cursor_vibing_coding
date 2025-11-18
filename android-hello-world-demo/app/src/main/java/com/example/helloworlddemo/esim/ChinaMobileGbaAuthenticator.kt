package com.example.helloworlddemo.esim

import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 根据中国移动 eSIM 要求执行 GBA（Generic Bootstrapping Architecture）认证的核心逻辑。
 *
 * 该实现遵循 3GPP 33.220 中的关键派生流程，并在此基础上组合出中国移动定制的
 * Authorization Header，便于后续向 EIS 或 SM-DP+ 发起安全请求。
 */
class ChinaMobileGbaAuthenticator(
    private val macAlgorithm: String = DEFAULT_MAC_ALGORITHM
) {

    /**
     * 执行一次完整的 GBA 认证，返回派生出的密钥与 Authorization 头部。
     */
    fun authenticate(request: ChinaMobileGbaRequest): ChinaMobileGbaResult {
        request.validate()

        val ks = deriveKs(request)
        val ksNaf = deriveKsNaf(ks, request)
        val token = buildAuthToken(ksNaf, request)
        val expiresAt = request.epochSeconds + request.lifetimeSeconds
        val header = buildAuthorizationHeader(request, token, expiresAt)

        return ChinaMobileGbaResult(
            ks = ks,
            ksNaf = ksNaf,
            authToken = token,
            authorizationHeader = header,
            expiresAtEpochSeconds = expiresAt
        )
    }

    private fun deriveKs(request: ChinaMobileGbaRequest): ByteArray {
        val key = request.ck + request.ik
        val randBytes = request.rand
        val impiBytes = request.impi.toByteArray(UTF8)
        return kdf(key, FC_KS, randBytes, impiBytes)
    }

    private fun deriveKsNaf(ks: ByteArray, request: ChinaMobileGbaRequest): ByteArray {
        val impiBytes = request.impi.toByteArray(UTF8)
        val nafBytes = request.nafId.toByteArray(UTF8)
        val typeBytes = request.gbaType.tag.toByteArray(UTF8)
        return kdf(ks, FC_KS_NAF, impiBytes, nafBytes, typeBytes)
    }

    private fun buildAuthToken(ksNaf: ByteArray, request: ChinaMobileGbaRequest): String {
        val payload = ByteArrayOutputStream().apply {
            write(request.btid.toByteArray(UTF8))
            write(request.nonce.toByteArray(UTF8))
            write(request.rand)
            write(request.autn)
            write(request.res)
            write(request.epochSeconds.toString().toByteArray(UTF8))
        }.toByteArray()

        val mac = hmac(ksNaf, payload)
        return BASE64_URL_ENCODER.encodeToString(mac)
    }

    private fun buildAuthorizationHeader(
        request: ChinaMobileGbaRequest,
        token: String,
        expiresAt: Long
    ): String = buildString {
        append("GBA ")
        append("btid=\"").append(request.btid).append("\",")
        append("nonce=\"").append(request.nonce).append("\",")
        append("token=\"").append(token).append("\",")
        append("expiry=\"").append(expiresAt).append("\"")
    }

    private fun kdf(key: ByteArray, fc: Byte, vararg params: ByteArray): ByteArray {
        val mac = Mac.getInstance(macAlgorithm)
        mac.init(SecretKeySpec(key, macAlgorithm))
        mac.update(byteArrayOf(fc))
        params.forEach { param ->
            mac.update(param)
            mac.update(lengthBytes(param.size))
        }
        return mac.doFinal()
    }

    private fun hmac(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance(macAlgorithm)
        mac.init(SecretKeySpec(key, macAlgorithm))
        return mac.doFinal(data)
    }

    private fun lengthBytes(byteLength: Int): ByteArray {
        val bitLength = byteLength shl 3
        val msb = (bitLength ushr 8) and 0xFF
        val lsb = bitLength and 0xFF
        return byteArrayOf(msb.toByte(), lsb.toByte())
    }

    companion object {
        private const val DEFAULT_MAC_ALGORITHM = "HmacSHA256"
        private const val FC_KS: Byte = 0x6A
        private const val FC_KS_NAF: Byte = 0x6B
        private val BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding()
        private val UTF8 = Charsets.UTF_8

        private fun ChinaMobileGbaRequest.validate() {
            require(rand.size == RAND_LENGTH) { "RAND must be $RAND_LENGTH bytes" }
            require(autn.size == AUTN_LENGTH) { "AUTN must be $AUTN_LENGTH bytes" }
            require(ck.size == KEY_LENGTH) { "CK must be $KEY_LENGTH bytes" }
            require(ik.size == KEY_LENGTH) { "IK must be $KEY_LENGTH bytes" }
            require(res.isNotEmpty()) { "RES cannot be empty" }
            require(impi.isNotBlank()) { "IMPI cannot be blank" }
            require(btid.isNotBlank()) { "BTID cannot be blank" }
            require(nafId.isNotBlank()) { "NAF ID cannot be blank" }
            require(nonce.isNotBlank()) { "Nonce cannot be blank" }
            require(epochSeconds > 0) { "epochSeconds must be positive" }
            require(lifetimeSeconds in 1..MAX_LIFETIME_SECONDS) {
                "lifetimeSeconds must be between 1 and $MAX_LIFETIME_SECONDS"
            }
        }

        private const val RAND_LENGTH = 16
        private const val AUTN_LENGTH = 16
        private const val KEY_LENGTH = 16
        private const val MAX_LIFETIME_SECONDS = 86_400L
    }
}

data class ChinaMobileGbaRequest(
    val rand: ByteArray,
    val autn: ByteArray,
    val res: ByteArray,
    val ck: ByteArray,
    val ik: ByteArray,
    val impi: String,
    val btid: String,
    val nafId: String,
    val nonce: String,
    val epochSeconds: Long,
    val lifetimeSeconds: Long = DEFAULT_LIFETIME_SECONDS,
    val gbaType: GbaType = GbaType.ME
) {
    companion object {
        const val DEFAULT_LIFETIME_SECONDS: Long = 600L
    }
}

data class ChinaMobileGbaResult(
    val ks: ByteArray,
    val ksNaf: ByteArray,
    val authToken: String,
    val authorizationHeader: String,
    val expiresAtEpochSeconds: Long
)

enum class GbaType(val tag: String) {
    ME("gba-me"),
    UICC("gba-u")
}
