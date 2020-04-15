package io.tanker.identity

internal const val AppPublicKeySize = 32
internal const val AppCreationNature = 1
internal const val AuthorSize = 32
internal const val UserSecretSize = 32
internal const val UserSecretHashSize = 16

internal fun hashUserId(appId: ByteArray, userId: String): ByteArray {
    var toHash = userId.toByteArray()
    toHash += appId
    return genericHash(toHash)
}

internal fun generateUserSecret(userId: ByteArray): ByteArray {
    val randomBuf = LazySodium.randomBytesBuf(UserSecretSize-1)
    val toHash = randomBuf + userId

    val outHash = ByteArray(UserSecretHashSize)
    LazySodium.cryptoGenericHash(outHash, outHash.size, toHash, toHash.size.toLong())

    return randomBuf + byteArrayOf(outHash[0])
}

internal fun getAppIdFromAppSecret(appSecret: ByteArray): ByteArray {
    val appPubKey = appSecret.sliceArray((appSecret.size - AppPublicKeySize) until appSecret.size)
    val toHash = byteArrayOf(AppCreationNature.toByte()) + ByteArray(AuthorSize) + appPubKey
    return genericHash(toHash)
}
