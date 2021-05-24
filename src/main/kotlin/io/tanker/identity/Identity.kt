package io.tanker.identity

import java.io.ByteArrayInputStream
import java.util.*
import javax.json.Json

class Identity {
    companion object {
        @JvmStatic
        fun createIdentity(appId: String, appSecret: String, userId: String): String {
            val appIdB = Base64.getDecoder().decode(appId)
            val appSecretB = Base64.getDecoder().decode(appSecret)
            val hashedUserId = hashUserId(appIdB, userId)

            val appIdFromSecret = getAppIdFromAppSecret(appSecretB)
            if (!(appIdFromSecret contentEquals appIdB))
                throw IllegalArgumentException("appId does not match appSecret")

            val ephemeralKeyPair = LazySodium.cryptoSignKeypair()
            val toSign = ephemeralKeyPair.publicKey.asBytes + hashedUserId
            val delegationSignature = cryptoSignDetached(toSign, appSecretB)
            val userSecret = generateUserSecret(hashedUserId)

            return serializedOrderedJsonB64(
                "trustchain_id" to appId,
                "target" to "user",
                "value" to toBase64(hashedUserId),
                "user_secret" to toBase64(userSecret),
                "ephemeral_public_signature_key" to toBase64(ephemeralKeyPair.publicKey.asBytes),
                "ephemeral_private_signature_key" to toBase64(ephemeralKeyPair.secretKey.asBytes),
                "delegation_signature" to toBase64(delegationSignature),
            )
        }

        @JvmStatic
        fun createProvisionalIdentity(appId: String, email: String): String {
            val signatureKeyPair = LazySodium.cryptoSignKeypair()
            val encryptionKeyPair = LazySodium.cryptoBoxKeypair()

            return serializedOrderedJsonB64(
                "trustchain_id" to appId,
                "target" to "email",
                "value" to email,
                "public_encryption_key" to toBase64(encryptionKeyPair.publicKey.asBytes),
                "private_encryption_key" to toBase64(encryptionKeyPair.secretKey.asBytes),
                "public_signature_key" to toBase64(signatureKeyPair.publicKey.asBytes),
                "private_signature_key" to toBase64(signatureKeyPair.secretKey.asBytes),
            )
        }

        @JvmStatic
        fun getPublicIdentity(identity: String): String {
            val identityObj = Json.createReader(ByteArrayInputStream(fromBase64(identity))).readObject()

            return if (identityObj.getString("target") == "user") {
                serializedOrderedJsonB64(
                    "trustchain_id" to identityObj.getString("trustchain_id"),
                    "target" to "user",
                    "value" to identityObj.getString("value"),
                )
            } else if (identityObj.containsKey("public_signature_key") && identityObj.containsKey("public_encryption_key")) {
                serializedOrderedJsonB64(
                    "trustchain_id" to identityObj.getString("trustchain_id"),
                    "target" to identityObj.getString("target"),
                    "value" to identityObj.getString("value"),
                    "public_signature_key" to identityObj.getString("public_signature_key"),
                    "public_encryption_key" to identityObj.getString("public_encryption_key"),
                )
            } else {
                throw IllegalArgumentException("not a valid Tanker identity")
            }
        }
    }
}
