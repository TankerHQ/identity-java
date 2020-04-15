package io.tanker.identity;

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

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
        String identity = Identity.createIdentity(appId, appSecret, "alice");

        JsonObject identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();
        byte[] userId = Base64.getDecoder().decode(identityObj.getString("value"));
        byte[] ephemeralPublicKey = Base64.getDecoder().decode(identityObj.getString("ephemeral_public_signature_key"));
        byte[] delegationSignature = Base64.getDecoder().decode(identityObj.getString("delegation_signature"));

        ByteArrayOutputStream signedBuffer = new ByteArrayOutputStream();
        signedBuffer.write(ephemeralPublicKey);
        signedBuffer.write(userId);
        byte[] signed = signedBuffer.toByteArray();

        assertEquals(identityObj.getString("target"), "user");
        assertEquals(identityObj.getString("trustchain_id"), appId);
        assertTrue(LazySodium.cryptoSignVerifyDetached(delegationSignature, signed, signed.length, Base64.getDecoder().decode(appPublicKey)));
    }

    @Test
    public void testCreateProvisionalIdentity() {
        String email = "alice@tanker.io";
        String identity = Identity.createProvisionalIdentity(appId, email);

        JsonObject identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();

        assertEquals(identityObj.getString("trustchain_id"), appId);
        assertEquals(identityObj.getString("target"), "email");
        assertEquals(identityObj.getString("value"), email);
    }

    @Test
    public void testGetPublicIdentity() {
        String identity = Identity.createIdentity(appId, appSecret, "alice");
        String publicIdentity = Identity.getPublicIdentity(identity);

        JsonObject identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();
        JsonObject publicIdentityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(publicIdentity))).readObject();

        assertEquals(publicIdentityObj.getString("value"), identityObj.getString("value"));
        assertEquals(publicIdentityObj.getString("target"), "user");
        assertEquals(publicIdentityObj.getString("trustchain_id"), appId);
    }

    @Test
    public void testGetPublicProvisionalIdentity() {
        String email = "alice@tanker.io";
        String identity = Identity.createProvisionalIdentity(appId, email);
        String publicIdentity = Identity.getPublicIdentity(identity);

        JsonObject identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();
        JsonObject publicIdentityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(publicIdentity))).readObject();

        assertEquals(publicIdentityObj.getString("public_signature_key"), identityObj.getString("public_signature_key"));
        assertEquals(publicIdentityObj.getString("public_encryption_key"), identityObj.getString("public_encryption_key"));
        assertEquals(publicIdentityObj.getString("target"), "email");
        assertEquals(publicIdentityObj.getString("trustchain_id"), appId);
    }
}
