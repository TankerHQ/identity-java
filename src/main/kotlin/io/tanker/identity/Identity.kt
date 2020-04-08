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
    }
}
