package com.m2m.management.utils;

import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.AEADBadTagException;
import java.util.Base64;

import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class AesGcmUtilTest {
    private static final byte[] KEY = Base64.getDecoder()
            .decode("NWEzZTNmOGY1YTNlM2Y1YTNlM2Y4ZjVhM2UzZjVhM2U=");

    @Test
    public void readsKeyFromCryptoServiceBinding() {
        String services = "{\"crypto\":[{\"binding_name\":\"edge365-secret-dev\","
                + "\"label\":\"crypto-key\",\"credentials\":{\"key_id\":\"key-2026-v1\","
                + "\"algorithm\":\"AES-256-GCM\","
                + "\"key_base64\":\"NWEzZTNmOGY1YTNlM2Y1YTNlM2Y4ZjVhM2UzZjVhM2U=\"}}]}";

        assertArrayEquals(KEY, AesGcmUtil.parseEncryptionKey(services));
    }

    @Test
    public void encryptsAndDecryptsUtf8WithRandomNonce() throws Exception {
        assumeAes256IsAvailable();
        String plaintext = "明文 with UTF-8";

        String first = AesGcmUtil.encrypt(KEY, plaintext);
        String second = AesGcmUtil.encrypt(KEY, plaintext);

        assertEquals(plaintext, AesGcmUtil.decrypt(KEY, first));
        assertEquals(plaintext, AesGcmUtil.decrypt(KEY, second));
        assertNotEquals(first, second);
    }

    @Test
    public void rejectsCiphertextWithInvalidAuthenticationTag() throws Exception {
        assumeAes256IsAvailable();
        byte[] encoded = Base64.getDecoder().decode(AesGcmUtil.encrypt(KEY, "plaintext"));
        encoded[encoded.length - 1] ^= 1;

        try {
            AesGcmUtil.decrypt(KEY, Base64.getEncoder().encodeToString(encoded));
            fail("Expected authentication failure");
        } catch (AEADBadTagException expected) {
            // Expected: GCM authentication must reject modified ciphertext.
        }
    }

    private void assumeAes256IsAvailable() throws Exception {
        assumeTrue("AES-256 requires unlimited-strength cryptography policy",
                Cipher.getMaxAllowedKeyLength("AES/GCM/NoPadding") >= 256);
    }
}
