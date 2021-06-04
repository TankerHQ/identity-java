package io.tanker.identity

import javax.json.Json

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
    val randomBuf = LazySodium.randomBytesBuf(UserSecretSize - 1)
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

internal fun jsonKeyOrder(key: String): Int {
    val jsonOrder = mapOf(
        "trustchain_id" to 1,
        "target" to 2,
        "value" to 3,
        "delegation_signature" to 4,
        "ephemeral_public_signature_key" to 5,
        "ephemeral_private_signature_key" to 6,
        "user_secret" to 7,
        "public_encryption_key" to 8,
        "private_encryption_key" to 9,
        "public_signature_key" to 10,
        "private_signature_key" to 11,
    )
    return jsonOrder[key]!!
}

/// NOTE: We only handle String JSON values, because the createJsonObjectBuilderBeanFactory is not polymorphic...
internal fun serializedOrderedJsonB64(vararg properties: Pair<String, String>): String {
    val json = sortedMapOf(compareBy { jsonKeyOrder(it) }, *properties)
        .asIterable()
        .fold(Json.createObjectBuilder()) { i, (k, v) ->
            i.add(k, v)
        }
        .build()
        .toString()
    return toBase64(json.toByteArray())
}
