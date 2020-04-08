package io.tanker.identity

import org.junit.Test

import org.junit.Assert.*

// We test `internal` functions here, we can't call them from Java, even from the same package
// So this is a Kotlin test
class UtilsKtTest {
    @Test
    fun testHashUserId() {
        val alice = "alice"
        val appId = "tpoxyNzh0hU9G2i9agMvHyyd+pO6zGCjO9BfhrCLjd4="
        val obfuscatedAlice = "hGTvBb8TQyyESmWF41BQJRfRQ2i3YyCwf6j89p3YM2M="
        assertEquals(toBase64(hashUserId(fromBase64(appId), alice)), obfuscatedAlice)
    }
}
