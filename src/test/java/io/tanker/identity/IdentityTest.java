package io.tanker.identity;

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import org.junit.Test;

import javax.json.Json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IdentityTest {
    private static String appId = "tpoxyNzh0hU9G2i9agMvHyyd+pO6zGCjO9BfhrCLjd4=";
    private static String appSecret = "cTMoGGUKhwN47ypq4xAXAtVkNWeyUtMltQnYwJhxWYSvqjPVGmXd2wwa7y17QtPTZhn8bxb015CZC/e4ZI7+MQ==";
    private static String appPublicKey = "r6oz1Rpl3dsMGu8te0LT02YZ/G8W9NeQmQv3uGSO/jE=";

    private static LazySodiumJava LazySodium = new LazySodiumJava(new SodiumJava());

    @Test
    public void testCreateIdentity() throws IOException {
        var identity = Identity.createIdentity(appId, appSecret, "alice");
        var reader = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity)));

        var identityObj = reader.readObject();
        var userId = Base64.getDecoder().decode(identityObj.getString("value"));
        var ephemeralPublicKey = Base64.getDecoder().decode(identityObj.getString("ephemeral_public_signature_key"));
        var delegationSignature = Base64.getDecoder().decode(identityObj.getString("delegation_signature"));

        var signedBuffer = new ByteArrayOutputStream();
        signedBuffer.write(ephemeralPublicKey);
        signedBuffer.write(userId);
        var signed = signedBuffer.toByteArray();

        assertEquals(identityObj.getString("target"), "user");
        assertEquals(identityObj.getString("trustchain_id"), appId);
        assertTrue(LazySodium.cryptoSignVerifyDetached(delegationSignature, signed, signed.length, Base64.getDecoder().decode(appPublicKey)));
    }

    @Test
    public void testCreateProvisionalIdentity() {
        var email = "alice@tanker.io";
        var identity = Identity.createProvisionalIdentity(appId, email);

        var identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();

        assertEquals(identityObj.getString("trustchain_id"), appId);
        assertEquals(identityObj.getString("target"), "email");
        assertEquals(identityObj.getString("value"), email);
    }

    @Test
    public void testGetPublicIdentity() {
        var identity = Identity.createIdentity(appId, appSecret, "alice");
        var publicIdentity = Identity.getPublicIdentity(identity);

        var identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();
        var publicIdentityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(publicIdentity))).readObject();

        assertEquals(publicIdentityObj.getString("value"), identityObj.getString("value"));
        assertEquals(publicIdentityObj.getString("target"), "user");
        assertEquals(publicIdentityObj.getString("trustchain_id"), appId);
    }
}
