package com.m2m.management.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.m2m.management.constant.SystemEnvConstant;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public final class AesGcmUtil {
    private static final String SERVICE_NAME = "crypto";
    private static final String SERVICE_LABEL = "crypto-key";
    private static final String ALGORITHM = "AES-256-GCM";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH_BYTES = 32;
    private static final int NONCE_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;
    private static final int TAG_LENGTH_BYTES = TAG_LENGTH_BITS / 8;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private AesGcmUtil() {
    }

    public static byte[] getEncryptionKey() {
        String services = System.getenv(SystemEnvConstant.SERVICES_key);
        if (services == null || services.trim().isEmpty()) {
            throw new IllegalStateException("Environment variable ENSAAS_SERVICES is not set");
        }
        return parseEncryptionKey(services);
    }

    static byte[] parseEncryptionKey(String services) {
        final JSONObject serviceBindings;
        try {
            serviceBindings = JSON.parseObject(services);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Environment variable ENSAAS_SERVICES is not valid JSON", e);
        }
        if (serviceBindings == null) {
            throw new IllegalStateException("Environment variable ENSAAS_SERVICES is not a JSON object");
        }

        JSONArray cryptoBindings = serviceBindings.getJSONArray(SERVICE_NAME);
        if (cryptoBindings == null) {
            throw new IllegalStateException("No crypto service binding found in ENSAAS_SERVICES");
        }

        for (int i = 0; i < cryptoBindings.size(); i++) {
            JSONObject binding = cryptoBindings.getJSONObject(i);
            if (binding == null || !SERVICE_LABEL.equals(binding.getString("label"))) {
                continue;
            }

            JSONObject credentials = binding.getJSONObject("credentials");
            if (credentials == null) {
                throw new IllegalStateException("Crypto service binding has no credentials");
            }
            if (!ALGORITHM.equals(credentials.getString("algorithm"))) {
                throw new IllegalStateException("Crypto service binding must use " + ALGORITHM);
            }

            String encodedKey = credentials.getString("key_base64");
            if (encodedKey == null || encodedKey.trim().isEmpty()) {
                throw new IllegalStateException("Crypto service binding has no key_base64");
            }

            final byte[] key;
            try {
                key = Base64.getDecoder().decode(encodedKey);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Crypto service key_base64 is not valid Base64", e);
            }
            validateKey(key);
            return key;
        }

        throw new IllegalStateException("No crypto service binding with label " + SERVICE_LABEL + " found");
    }

    public static String encrypt(String plaintext) throws GeneralSecurityException {
        return encrypt(getEncryptionKey(), plaintext);
    }

    public static String decrypt(String ciphertext) throws GeneralSecurityException {
        return decrypt(getEncryptionKey(), ciphertext);
    }

    public static String encryptIfNeeded(String value) throws GeneralSecurityException {
        if (value == null || value.isEmpty()) {
            return value;
        }
        try {
            decrypt(value);
            return value;
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return encrypt(value);
        }
    }

    static String encrypt(byte[] key, String plaintext) throws GeneralSecurityException {
        validateKey(key);
        ensureAes256IsSupported();
        if (plaintext == null) {
            throw new IllegalArgumentException("plaintext must not be null");
        }

        byte[] nonce = new byte[NONCE_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(nonce);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
        byte[] ciphertextAndTag = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] encoded = ByteBuffer.allocate(nonce.length + ciphertextAndTag.length)
                .put(nonce)
                .put(ciphertextAndTag)
                .array();
        return Base64.getEncoder().encodeToString(encoded);
    }

    static String decrypt(byte[] key, String ciphertext) throws GeneralSecurityException {
        validateKey(key);
        ensureAes256IsSupported();
        if (ciphertext == null) {
            throw new IllegalArgumentException("ciphertext must not be null");
        }

        byte[] encoded = Base64.getDecoder().decode(ciphertext);
        if (encoded.length < NONCE_LENGTH_BYTES + TAG_LENGTH_BYTES) {
            throw new IllegalArgumentException("ciphertext has an invalid format");
        }

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(TAG_LENGTH_BITS, encoded, 0, NONCE_LENGTH_BYTES));
        byte[] plaintext = cipher.doFinal(encoded, NONCE_LENGTH_BYTES,
                encoded.length - NONCE_LENGTH_BYTES);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    private static void validateKey(byte[] key) {
        if (key == null || key.length != KEY_LENGTH_BYTES) {
            throw new IllegalArgumentException("AES-256-GCM requires a 32-byte key");
        }
    }

    private static void ensureAes256IsSupported() throws GeneralSecurityException {
        if (Cipher.getMaxAllowedKeyLength(CIPHER_TRANSFORMATION) < KEY_LENGTH_BYTES * 8) {
            throw new GeneralSecurityException(
                    "AES-256-GCM requires the Java 8 unlimited-strength cryptography policy");
        }
    }
}
