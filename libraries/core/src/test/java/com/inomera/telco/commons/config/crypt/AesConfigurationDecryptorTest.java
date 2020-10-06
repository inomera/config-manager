package com.inomera.telco.commons.config.crypt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Serdar Kuzucu
 */
class AesConfigurationDecryptorTest {
    private static final String SECRET_KEY_16_BYTE = Base64.getEncoder().encodeToString("1234567890123456".getBytes());
    private static final String SECRET_KEY_32_BYTE = "dYZ0iI8hotQGI73jQoqPK0K5TigOHelNBmPMjazlme4=";
    private static final String SECRET_KEY_INVALID = "dYZ0iI8hotQaGI73jd11QoqPK0K5TigOHelNBmPMjazlme4=";

    @Test
    @DisplayName("Should Decrypt With AES/CBC/PKCS5PADDING and 16 byte secret key")
    void shouldDecryptWith_AES_CBC_PKCS5PADDING_and16ByteSecretKey() throws Exception {
        final String plain = "verySecretValueB";
        final String encryptedValue = encryptValue(SECRET_KEY_16_BYTE, plain);

        final AesConfigurationDecryptor decryptor = new AesConfigurationDecryptor(SECRET_KEY_16_BYTE);
        final String decrypted = decryptor.decrypt("anything-is-possible", encryptedValue);
        assertEquals(plain, decrypted);
    }

    @Test
    @DisplayName("Should Decrypt With AES/CBC/PKCS5PADDING and 32 byte secret key")
    void shouldDecryptWith_AES_CBC_PKCS5PADDING_and32ByteSecretKey() throws Exception {
        final String plain = "verySecretValueA";
        final String encryptedValue = encryptValue(SECRET_KEY_32_BYTE, plain);

        final AesConfigurationDecryptor decryptor = new AesConfigurationDecryptor(SECRET_KEY_32_BYTE);
        final String decrypted = decryptor.decrypt("anything-is-possible", encryptedValue);
        assertEquals(plain, decrypted);
    }

    @Test
    @DisplayName("Should return original value when decryption fails")
    void shouldReturnOriginalValueWhenDecryptionFails() throws Exception {
        final String plain = "verySecretValueX";
        final String encryptedValue = encryptValue(SECRET_KEY_32_BYTE, plain);

        final AesConfigurationDecryptor decryptor = new AesConfigurationDecryptor(SECRET_KEY_INVALID);
        final String decrypted = decryptor.decrypt("anything-is-possible", encryptedValue);
        assertEquals(encryptedValue, decrypted);
    }

    private String encryptValue(String secretKey, String value) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        final byte[] decodedSecretKey = Base64.getDecoder().decode(secretKey);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(decodedSecretKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        final byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
