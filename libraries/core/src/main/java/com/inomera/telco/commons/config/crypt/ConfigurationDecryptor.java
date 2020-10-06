package com.inomera.telco.commons.config.crypt;

/**
 * @author Serdar Kuzucu
 */
public interface ConfigurationDecryptor {
    String decrypt(String key, String value);
}
