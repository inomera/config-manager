package com.inomera.telco.commons.config.crypt;

import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;

/**
 * @author Serdar Kuzucu
 */
@RequiredArgsConstructor
public class JasyptConfigurationDecryptor implements ConfigurationDecryptor {
    private final StringEncryptor stringEncryptor;

    @Override
    public String decrypt(String key, String value) {
        return stringEncryptor.decrypt(value);
    }
}
