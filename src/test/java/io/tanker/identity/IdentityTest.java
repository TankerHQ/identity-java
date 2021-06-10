package io.tanker.identity;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.function.Function;

import static io.tanker.identity.Identity.upgradeIdentity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IdentityTest {
    private static final String appId = "tpoxyNzh0hU9G2i9agMvHyyd+pO6zGCjO9BfhrCLjd4=";
    private static final String appSecret = "cTMoGGUKhwN47ypq4xAXAtVkNWeyUtMltQnYwJhxWYSvqjPVGmXd2wwa7y17QtPTZhn8bxb015CZC/e4ZI7+MQ==";

    private static final String PERMANENT_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJ1c2VyIiwidmFsdWUiOiJSRGEwZXE0WE51ajV0VjdoZGFwak94aG1oZVRoNFFCRE5weTRTdnk5WG9rPSIsImRlbGVnYXRpb25fc2lnbmF0dXJlIjoiVTlXUW9sQ3ZSeWpUOG9SMlBRbWQxV1hOQ2kwcW1MMTJoTnJ0R2FiWVJFV2lyeTUya1d4MUFnWXprTHhINmdwbzNNaUE5cisremhubW9ZZEVKMCtKQ3c9PSIsImVwaGVtZXJhbF9wdWJsaWNfc2lnbmF0dXJlX2tleSI6IlhoM2kweERUcHIzSFh0QjJRNTE3UUt2M2F6TnpYTExYTWRKRFRTSDRiZDQ9IiwiZXBoZW1lcmFsX3ByaXZhdGVfc2lnbmF0dXJlX2tleSI6ImpFRFQ0d1FDYzFERndvZFhOUEhGQ2xuZFRQbkZ1Rm1YaEJ0K2lzS1U0WnBlSGVMVEVOT212Y2RlMEhaRG5YdEFxL2RyTTNOY3N0Y3gwa05OSWZodDNnPT0iLCJ1c2VyX3NlY3JldCI6IjdGU2YvbjBlNzZRVDNzMERrdmV0UlZWSmhYWkdFak94ajVFV0FGZXh2akk9In0=";
    private static final String PROVISIONAL_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJlbWFpbCIsInZhbHVlIjoiYnJlbmRhbi5laWNoQHRhbmtlci5pbyIsInB1YmxpY19lbmNyeXB0aW9uX2tleSI6Ii8yajRkSTNyOFBsdkNOM3VXNEhoQTV3QnRNS09jQUNkMzhLNk4wcSttRlU9IiwicHJpdmF0ZV9lbmNyeXB0aW9uX2tleSI6IjRRQjVUV212Y0JyZ2V5RERMaFVMSU5VNnRicUFPRVE4djlwakRrUGN5YkE9IiwicHVibGljX3NpZ25hdHVyZV9rZXkiOiJXN1FFUUJ1OUZYY1hJcE9ncTYydFB3Qml5RkFicFQxckFydUQwaC9OclRBPSIsInByaXZhdGVfc2lnbmF0dXJlX2tleSI6IlVtbll1dmRUYUxZRzBhK0phRHBZNm9qdzQvMkxsOHpzbXJhbVZDNGZ1cVJidEFSQUc3MFZkeGNpazZDcnJhMC9BR0xJVUJ1bFBXc0N1NFBTSDgydE1BPT0ifQ==";
    private static final String PUBLIC_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJ1c2VyIiwidmFsdWUiOiJSRGEwZXE0WE51ajV0VjdoZGFwak94aG1oZVRoNFFCRE5weTRTdnk5WG9rPSJ9";
    private static final String PUBLIC_PROVISIONAL_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJlbWFpbCIsInZhbHVlIjoiYnJlbmRhbi5laWNoQHRhbmtlci5pbyIsInB1YmxpY19lbmNyeXB0aW9uX2tleSI6Ii8yajRkSTNyOFBsdkNOM3VXNEhoQTV3QnRNS09jQUNkMzhLNk4wcSttRlU9IiwicHVibGljX3NpZ25hdHVyZV9rZXkiOiJXN1FFUUJ1OUZYY1hJcE9ncTYydFB3Qml5RkFicFQxckFydUQwaC9OclRBPSJ9";

    private static final LazySodiumJava LazySodium = new LazySodiumJava(new SodiumJava());

    @Test
    public void testIdentitySerializationRoundtrip() throws IOException {
        String upgradedPermanent = upgradeIdentity(PERMANENT_IDENTITY);
        String upgradedProvisional = upgradeIdentity(PROVISIONAL_IDENTITY);
        String upgradedPublic = upgradeIdentity(PUBLIC_IDENTITY);
        String upgradedPublicProvisional = upgradeIdentity(PUBLIC_PROVISIONAL_IDENTITY);

        assertEquals(upgradedPermanent, PERMANENT_IDENTITY);
        assertEquals(upgradedProvisional, PROVISIONAL_IDENTITY);
        assertEquals(upgradedPublic, PUBLIC_IDENTITY);
        assertEquals(upgradedPublicProvisional, PUBLIC_PROVISIONAL_IDENTITY);
    }

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
        String appPublicKey = "r6oz1Rpl3dsMGu8te0LT02YZ/G8W9NeQmQv3uGSO/jE=";
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
        assertEquals(publicIdentityObj.getString("target"), "hashed_email");
        assertEquals(publicIdentityObj.getString("trustchain_id"), appId);
    }

    @Test
    public void testUpgradeIdentity() {
        Function<String, JsonObject> unJson = (identity) ->
                Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();

        String email = "alice@tanker.io";
        String identity = Identity.createIdentity(appId, appSecret, "alice");
        String publicIdentity = Identity.getPublicIdentity(identity);
        String provIdentity = Identity.createProvisionalIdentity(appId, email);
        String publicProvIdentity = Identity.getPublicIdentity(provIdentity);

        String newIdentity = upgradeIdentity(identity);
        String newPublicIdentity = upgradeIdentity(publicIdentity);
        String newProvIdentity = upgradeIdentity(provIdentity);
        String newPublicProvIdentity = upgradeIdentity(publicProvIdentity);

        assertEquals(unJson.apply(identity), unJson.apply(newIdentity));
        assertEquals(unJson.apply(publicIdentity), unJson.apply(newPublicIdentity));
        assertEquals(unJson.apply(provIdentity), unJson.apply(newProvIdentity));
        assertEquals(unJson.apply(publicProvIdentity), unJson.apply(newPublicProvIdentity));
    }
}
