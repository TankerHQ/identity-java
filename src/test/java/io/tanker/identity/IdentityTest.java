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

    private static final String USER_PHONE = "+33611223344";
    private static final String HASHED_USER_PHONE = "JeaiQAh8x7jcioU2m4ihy+CsHJlyW+4VVSSs5SHFUTw=";
    private static final String PERMANENT_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJ1c2VyIiwidmFsdWUiOiJSRGEwZXE0WE51ajV0VjdoZGFwak94aG1oZVRoNFFCRE5weTRTdnk5WG9rPSIsImRlbGVnYXRpb25fc2lnbmF0dXJlIjoiVTlXUW9sQ3ZSeWpUOG9SMlBRbWQxV1hOQ2kwcW1MMTJoTnJ0R2FiWVJFV2lyeTUya1d4MUFnWXprTHhINmdwbzNNaUE5cisremhubW9ZZEVKMCtKQ3c9PSIsImVwaGVtZXJhbF9wdWJsaWNfc2lnbmF0dXJlX2tleSI6IlhoM2kweERUcHIzSFh0QjJRNTE3UUt2M2F6TnpYTExYTWRKRFRTSDRiZDQ9IiwiZXBoZW1lcmFsX3ByaXZhdGVfc2lnbmF0dXJlX2tleSI6ImpFRFQ0d1FDYzFERndvZFhOUEhGQ2xuZFRQbkZ1Rm1YaEJ0K2lzS1U0WnBlSGVMVEVOT212Y2RlMEhaRG5YdEFxL2RyTTNOY3N0Y3gwa05OSWZodDNnPT0iLCJ1c2VyX3NlY3JldCI6IjdGU2YvbjBlNzZRVDNzMERrdmV0UlZWSmhYWkdFak94ajVFV0FGZXh2akk9In0=";
    private static final String PROVISIONAL_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJlbWFpbCIsInZhbHVlIjoiYnJlbmRhbi5laWNoQHRhbmtlci5pbyIsInB1YmxpY19lbmNyeXB0aW9uX2tleSI6Ii8yajRkSTNyOFBsdkNOM3VXNEhoQTV3QnRNS09jQUNkMzhLNk4wcSttRlU9IiwicHJpdmF0ZV9lbmNyeXB0aW9uX2tleSI6IjRRQjVUV212Y0JyZ2V5RERMaFVMSU5VNnRicUFPRVE4djlwakRrUGN5YkE9IiwicHVibGljX3NpZ25hdHVyZV9rZXkiOiJXN1FFUUJ1OUZYY1hJcE9ncTYydFB3Qml5RkFicFQxckFydUQwaC9OclRBPSIsInByaXZhdGVfc2lnbmF0dXJlX2tleSI6IlVtbll1dmRUYUxZRzBhK0phRHBZNm9qdzQvMkxsOHpzbXJhbVZDNGZ1cVJidEFSQUc3MFZkeGNpazZDcnJhMC9BR0xJVUJ1bFBXc0N1NFBTSDgydE1BPT0ifQ==";
    private static final String PUBLIC_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJ1c2VyIiwidmFsdWUiOiJSRGEwZXE0WE51ajV0VjdoZGFwak94aG1oZVRoNFFCRE5weTRTdnk5WG9rPSJ9";
    private static final String OLD_PUBLIC_PROVISIONAL_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJlbWFpbCIsInZhbHVlIjoiYnJlbmRhbi5laWNoQHRhbmtlci5pbyIsInB1YmxpY19lbmNyeXB0aW9uX2tleSI6Ii8yajRkSTNyOFBsdkNOM3VXNEhoQTV3QnRNS09jQUNkMzhLNk4wcSttRlU9IiwicHVibGljX3NpZ25hdHVyZV9rZXkiOiJXN1FFUUJ1OUZYY1hJcE9ncTYydFB3Qml5RkFicFQxckFydUQwaC9OclRBPSJ9";
    private static final String PUBLIC_PROVISIONAL_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJoYXNoZWRfZW1haWwiLCJ2YWx1ZSI6IjB1MmM4dzhFSVpXVDJGelJOL3l5TTVxSWJFR1lUTkRUNVNrV1ZCdTIwUW89IiwicHVibGljX2VuY3J5cHRpb25fa2V5IjoiLzJqNGRJM3I4UGx2Q04zdVc0SGhBNXdCdE1LT2NBQ2QzOEs2TjBxK21GVT0iLCJwdWJsaWNfc2lnbmF0dXJlX2tleSI6Ilc3UUVRQnU5RlhjWElwT2dxNjJ0UHdCaXlGQWJwVDFyQXJ1RDBoL05yVEE9In0=";
    private static final String PHONE_NUMBER_PROVISIONAL_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJwaG9uZV9udW1iZXIiLCJ2YWx1ZSI6IiszMzYxMTIyMzM0NCIsInB1YmxpY19lbmNyeXB0aW9uX2tleSI6Im42bTlYNUxmMFpuYXo4ZjArc2NoTElCTm0rcGlQaG5zWXZBdlh3MktFQXc9IiwicHJpdmF0ZV9lbmNyeXB0aW9uX2tleSI6InRWVFM5bkh4cjJNZFZ1VFI1Y2x3dzBFWGJ3aXM4SGl4Z1BJTmJRSngxVTQ9IiwicHVibGljX3NpZ25hdHVyZV9rZXkiOiJqcklEaWdTQ25BaTNHbDltSUFTbEFpU2hLQzdkQkxGVVpQOUN4TEdzYkg4PSIsInByaXZhdGVfc2lnbmF0dXJlX2tleSI6IlFIcWNMcjhicjZNM2JQblFtUWczcStxSENycDA1RGJjQnBMUGFUWlkwYTZPc2dPS0JJS2NDTGNhWDJZZ0JLVUNKS0VvTHQwRXNWUmsvMExFc2F4c2Z3PT0ifQ==";
    private static final String PHONE_NUMBER_PUBLIC_PROVISIONAL_IDENTITY = "eyJ0cnVzdGNoYWluX2lkIjoidHBveHlOemgwaFU5RzJpOWFnTXZIeXlkK3BPNnpHQ2pPOUJmaHJDTGpkND0iLCJ0YXJnZXQiOiJwaG9uZV9udW1iZXIiLCJ2YWx1ZSI6IkplYWlRQWg4eDdqY2lvVTJtNGloeStDc0hKbHlXKzRWVlNTczVTSEZVVHc9IiwicHVibGljX2VuY3J5cHRpb25fa2V5IjoibjZtOVg1TGYwWm5hejhmMCtzY2hMSUJObStwaVBobnNZdkF2WHcyS0VBdz0iLCJwdWJsaWNfc2lnbmF0dXJlX2tleSI6ImpySURpZ1NDbkFpM0dsOW1JQVNsQWlTaEtDN2RCTEZVWlA5Q3hMR3NiSDg9In0=";


    private static final LazySodiumJava LazySodium = new LazySodiumJava(new SodiumJava());

    @Test
    public void testIdentitySerializationRoundtrip() throws IOException {
        String upgradedPermanent = upgradeIdentity(PERMANENT_IDENTITY);
        String upgradedProvisional = upgradeIdentity(PROVISIONAL_IDENTITY);
        String upgradedPublic = upgradeIdentity(PUBLIC_IDENTITY);
        String upgradedPublicProvisional = upgradeIdentity(OLD_PUBLIC_PROVISIONAL_IDENTITY);

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

    @Test(expected = IllegalArgumentException.class)
    public void testCannotCreateProvisionalIdentityWithBadTarget() {
       Identity.createProvisionalIdentity(appId, "INVALID !", "xxx");
    }

    @Test
    public void testCreateProvisionalIdentity() {
        String email = "alice@tanker.io";
        String identity = Identity.createProvisionalIdentity(appId, "email", email);

        JsonObject identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();

        assertEquals(identityObj.getString("trustchain_id"), appId);
        assertEquals(identityObj.getString("target"), "email");
        assertEquals(identityObj.getString("value"), email);
    }

    @Test
    public void testCreateProvisionalPhoneNumberIdentity() {
        String phone = "+611223344";
        String identity = Identity.createProvisionalIdentity(appId, "phone_number", phone);

        JsonObject identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();

        assertEquals(identityObj.getString("trustchain_id"), appId);
        assertEquals(identityObj.getString("target"), "phone_number");
        assertEquals(identityObj.getString("value"), phone);
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
        String identity = Identity.createProvisionalIdentity(appId, "email", email);
        String publicIdentity = Identity.getPublicIdentity(identity);

        JsonObject identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();
        JsonObject publicIdentityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(publicIdentity))).readObject();

        assertEquals(publicIdentityObj.getString("public_signature_key"), identityObj.getString("public_signature_key"));
        assertEquals(publicIdentityObj.getString("public_encryption_key"), identityObj.getString("public_encryption_key"));
        assertEquals(publicIdentityObj.getString("target"), "hashed_email");
        assertEquals(publicIdentityObj.getString("trustchain_id"), appId);
    }

    @Test
    public void testParsePhoneNumberProvisionalIdentity() {
        JsonObject identityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(PHONE_NUMBER_PROVISIONAL_IDENTITY))).readObject();

        assertEquals(identityObj.getString("target"), "phone_number");
        assertEquals(identityObj.getString("value"), USER_PHONE);
        assertEquals(identityObj.getString("trustchain_id"), appId);
        assertEquals(identityObj.getString("public_signature_key"), "jrIDigSCnAi3Gl9mIASlAiShKC7dBLFUZP9CxLGsbH8=");
        assertEquals(identityObj.getString("public_encryption_key"), "n6m9X5Lf0Znaz8f0+schLIBNm+piPhnsYvAvXw2KEAw=");
        assertEquals(identityObj.getString("private_signature_key"), "QHqcLr8br6M3bPnQmQg3q+qHCrp05DbcBpLPaTZY0a6OsgOKBIKcCLcaX2YgBKUCJKEoLt0EsVRk/0LEsaxsfw==");
        assertEquals(identityObj.getString("private_encryption_key"), "tVTS9nHxr2MdVuTR5clww0EXbwis8HixgPINbQJx1U4=");
    }

    @Test
    public void testParsePhoneNumberPublicProvisionalIdentity() {
        String publicIdentity = Identity.getPublicIdentity(PHONE_NUMBER_PROVISIONAL_IDENTITY);
        assertEquals(publicIdentity, PHONE_NUMBER_PUBLIC_PROVISIONAL_IDENTITY);

        JsonObject publicIdentityObj = Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(publicIdentity))).readObject();

        assertEquals(publicIdentityObj.getString("target"), "phone_number");
        assertEquals(publicIdentityObj.getString("value"), HASHED_USER_PHONE);
        assertEquals(publicIdentityObj.getString("trustchain_id"), appId);
        assertEquals(publicIdentityObj.getString("public_signature_key"), "jrIDigSCnAi3Gl9mIASlAiShKC7dBLFUZP9CxLGsbH8=");
        assertEquals(publicIdentityObj.getString("public_encryption_key"), "n6m9X5Lf0Znaz8f0+schLIBNm+piPhnsYvAvXw2KEAw=");
    }

    @Test
    public void testUpgradeIdentity() {
        Function<String, JsonObject> unJson = (identity) ->
                Json.createReader(new ByteArrayInputStream(Base64.getDecoder().decode(identity))).readObject();

        String email = "alice@tanker.io";
        String identity = Identity.createIdentity(appId, appSecret, "alice");
        String publicIdentity = Identity.getPublicIdentity(identity);
        String provIdentity = Identity.createProvisionalIdentity(appId, "email", email);
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
