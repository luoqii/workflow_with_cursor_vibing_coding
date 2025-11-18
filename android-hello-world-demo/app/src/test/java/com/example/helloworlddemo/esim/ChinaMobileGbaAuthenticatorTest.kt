package com.example.helloworlddemo.esim

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.Locale

class ChinaMobileGbaAuthenticatorTest {

    private val authenticator = ChinaMobileGbaAuthenticator()

    @Test
    fun `authenticate returns deterministic materials`() {
        val request = requestFixture()
        val result = authenticator.authenticate(request)

        assertEquals(request.epochSeconds + request.lifetimeSeconds, result.expiresAtEpochSeconds)
        assertEquals("8f0da9aad40555ea22fea5ea4e734ff0b605aeabd399e522b6ee42db42b916e7", result.ks.toHexString())
        assertEquals("8050d932fd603a0dba743b85fca5bbbfe597eb5eca24e87728ca2941ed91b935", result.ksNaf.toHexString())
        assertEquals("27CDGA4C_cHpXtwB0BoF0qf5V6BKpgviOX94qLGCW3g", result.authToken)
        val expectedHeader =
            "GBA btid=\"CMCC-ESIM-987654321\",nonce=\"nonce-123456\",token=\"27CDGA4C_cHpXtwB0BoF0qf5V6BKpgviOX94qLGCW3g\",expiry=\"1723000900\""
        assertEquals(expectedHeader, result.authorizationHeader)
    }

    @Test
    fun `authenticate rejects invalid rand`() {
        val request = requestFixture(rand = hex("AAAA"))
        assertThrows(IllegalArgumentException::class.java) {
            authenticator.authenticate(request)
        }
    }

    private fun requestFixture(
        rand: ByteArray = hex("0123456789ABCDEFFEDCBA9876543210"),
        autn: ByteArray = hex("89ABCDEF01234567FEDCBA9876543210"),
        res: ByteArray = hex("A1A2A3A4A5A6"),
        ck: ByteArray = hex("465B5CE8B199B49FAA5F0A2EE238A6BC"),
        ik: ByteArray = hex("DC786B765A26C89F5DF5A94F14F97E0D"),
        impi: String = "460001357924680@ims.mnc000.mcc460.3gppnetwork.org",
        btid: String = "CMCC-ESIM-987654321",
        nafId: String = "nafid:5g.eid.chinamobile.com",
        nonce: String = "nonce-123456",
        epochSeconds: Long = 1_723_000_000L,
        lifetimeSeconds: Long = 900L,
        gbaType: GbaType = GbaType.ME
    ): ChinaMobileGbaRequest = ChinaMobileGbaRequest(
        rand = rand,
        autn = autn,
        res = res,
        ck = ck,
        ik = ik,
        impi = impi,
        btid = btid,
        nafId = nafId,
        nonce = nonce,
        epochSeconds = epochSeconds,
        lifetimeSeconds = lifetimeSeconds,
        gbaType = gbaType
    )
}

private fun hex(input: String): ByteArray {
    val normalized = input.replace("\\s".toRegex(), "").lowercase(Locale.US)
    require(normalized.length % 2 == 0) { "Hex string must have even length" }

    return ByteArray(normalized.length / 2) { index ->
        val start = index * 2
        normalized.substring(start, start + 2).toInt(16).toByte()
    }
}

private fun ByteArray.toHexString(): String =
    joinToString(separator = "") { each -> "%02x".format(each) }
