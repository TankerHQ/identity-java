package io.tanker.identity

import java.util.*

internal const val HashSize = 32
internal const val SignatureSize = 64

internal fun genericHash(toHash: ByteArray): ByteArray {
    val outHash = ByteArray(HashSize)
    LazySodium.cryptoGenericHash(outHash, outHash.size, toHash, toHash.size.toLong())
    return outHash
}

internal fun cryptoSignDetached(message: ByteArray, key: ByteArray): ByteArray {
    val out = ByteArray(SignatureSize)
    LazySodium.cryptoSignDetached(out, message, message.size.toLong(), key)
    return out
}

internal fun toBase64(m: ByteArray): String =
    Base64.getEncoder().encodeToString(m)

internal fun fromBase64(m: String): ByteArray =
    Base64.getDecoder().decode(m)
