package com.inomera.telco.commons.config.crypt;

import com.inomera.telco.commons.config.ConfigurationPostProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Serdar Kuzucu
 */
@RequiredArgsConstructor
public class DecryptingConfigurationPostProcessor implements ConfigurationPostProcessor {
    private final ConfigurationDecryptor configurationDecryptor;
    private final String encryptedValuePrefix;

    @Override
    public String postProcessConfiguration(String key, String value) {
        if (StringUtils.startsWith(value, encryptedValuePrefix)) {
            value = StringUtils.substringAfter(value, encryptedValuePrefix);
            return configurationDecryptor.decrypt(key, value);
        }
        return value;
    }
}
