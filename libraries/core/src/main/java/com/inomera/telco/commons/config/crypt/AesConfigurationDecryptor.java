package com.inomera.telco.commons.config.crypt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Serdar Kuzucu
 */
@Slf4j
@RequiredArgsConstructor
public class AesConfigurationDecryptor implements ConfigurationDecryptor {
    private static final String ALGORITHM = "AES";
    private static final String DEFAULT_ENCRYPTION_MODE = "AES/CBC/PKCS5PADDING";

    private final String encryptionMode;
    private final SecretKey secretKey;
    private final IvParameterSpec initializationVector;

    public AesConfigurationDecryptor(String secretKey) {
        this(DEFAULT_ENCRYPTION_MODE, createSecretKey(secretKey), emptyIV());
    }

    @Override
    public String decrypt(String key, String base64EncodedEncryptedValue) {
        try {
            final Cipher cipher = Cipher.getInstance(this.encryptionMode);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initializationVector);
            final byte[] encryptedValue = Base64.getDecoder().decode(base64EncodedEncryptedValue);
            final byte[] decrypted = cipher.doFinal(encryptedValue);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOG.error("Error in decryption! Resulting unencrypted value. error={}", e.getMessage(), e);
        }
        return base64EncodedEncryptedValue;
    }

    private static SecretKey createSecretKey(String base64Encoded) {
        final byte[] decodedSecretKey = Base64.getDecoder().decode(base64Encoded);
        return new SecretKeySpec(decodedSecretKey, ALGORITHM);
    }

    private static IvParameterSpec emptyIV() {
        return new IvParameterSpec(new byte[16]);
    }
}
