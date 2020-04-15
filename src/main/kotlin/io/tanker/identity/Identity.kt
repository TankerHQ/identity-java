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

            val identityJson = Json.createObjectBuilder()
                .add("trustchain_id", appId)
                .add("target", "user")
                .add("value", toBase64(hashedUserId))
                .add("user_secret", toBase64(userSecret))
                .add("ephemeral_public_signature_key", toBase64(ephemeralKeyPair.publicKey.asBytes))
                .add("ephemeral_private_signature_key", toBase64(ephemeralKeyPair.secretKey.asBytes))
                .add("delegation_signature", toBase64(delegationSignature))
                .build()
            return toBase64(identityJson.toString().toByteArray())
        }

        @JvmStatic
        fun createProvisionalIdentity(appId: String, email: String): String {
            val signatureKeyPair = LazySodium.cryptoSignKeypair()
            val encryptionKeyPair = LazySodium.cryptoBoxKeypair()

            val identityJson = Json.createObjectBuilder()
                .add("trustchain_id", appId)
                .add("target", "email")
                .add("value", email)
                .add("public_encryption_key", toBase64(encryptionKeyPair.publicKey.asBytes))
                .add("private_encryption_key", toBase64(encryptionKeyPair.secretKey.asBytes))
                .add("public_signature_key", toBase64(signatureKeyPair.publicKey.asBytes))
                .add("private_signature_key", toBase64(signatureKeyPair.secretKey.asBytes))
                .build()
            return toBase64(identityJson.toString().toByteArray())
        }

        @JvmStatic
        fun getPublicIdentity(identity: String): String {
            val identityObj = Json.createReader(ByteArrayInputStream(fromBase64(identity))).readObject()

            val publicIdentityObj =
                if (identityObj.getString("target") == "user") {
                    Json.createObjectBuilder()
                        .add("trustchain_id", identityObj.getString("trustchain_id"))
                        .add("target", "user")
                        .add("value", identityObj.getString("value"))
                        .build()
                } else if (identityObj.containsKey("public_signature_key") && identityObj.containsKey("public_encryption_key")) {
                    Json.createObjectBuilder()
                        .add("trustchain_id", identityObj.getString("trustchain_id"))
                        .add("target", identityObj.getString("target"))
                        .add("value", identityObj.getString("value"))
                        .add("public_signature_key", identityObj.getString("public_signature_key"))
                        .add("public_encryption_key", identityObj.getString("public_encryption_key"))
                        .build()
                } else {
                    throw IllegalArgumentException("not a valid Tanker identity")
                }

            return toBase64(publicIdentityObj.toString().toByteArray())
        }
    }
}
