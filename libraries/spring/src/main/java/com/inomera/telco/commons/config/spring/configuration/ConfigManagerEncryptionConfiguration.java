package com.inomera.telco.commons.config.spring.configuration;

import com.inomera.telco.commons.config.ConfigurationPostProcessor;
import com.inomera.telco.commons.config.crypt.ConfigurationDecryptor;
import com.inomera.telco.commons.config.crypt.DecryptingConfigurationPostProcessor;
import com.inomera.telco.commons.config.crypt.JasyptConfigurationDecryptor;
import com.inomera.telco.commons.config.spring.condition.ConfigManagerEncryptionEnabledCondition;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_CONFIGURATION_DECRYPTOR;
import static com.inomera.telco.commons.config.spring.BeanNames.BEAN_CM_JASYPT_ENCRYPTOR;

/**
 * @author Serdar Kuzucu
 */
@Configuration
@Conditional(ConfigManagerEncryptionEnabledCondition.class)
public class ConfigManagerEncryptionConfiguration {
    private static final String ENCRYPTED_VALUE_PREFIX = "{cipher}";

    @Bean
    public ConfigurationPostProcessor decrypt(
            @Qualifier(BEAN_CM_CONFIGURATION_DECRYPTOR) ConfigurationDecryptor configurationDecryptor) {
        return new DecryptingConfigurationPostProcessor(configurationDecryptor, ENCRYPTED_VALUE_PREFIX);
    }

    @Configuration
    @ConditionalOnClass(StringEncryptor.class)
    @ConditionalOnMissingBean(name = BEAN_CM_CONFIGURATION_DECRYPTOR)
    public static class JasyptStringEncryptorConfiguration {
        private static final int POOL_SIZE = 1;
        private static final String PROVIDER_NAME = "SunJCE";
        private static final String SALT_GENERATOR_CLASS_NAME = "org.jasypt.salt.RandomSaltGenerator";

        @Bean(name = BEAN_CM_CONFIGURATION_DECRYPTOR)
        public ConfigurationDecryptor configurationDecryptor(
                @Qualifier(BEAN_CM_JASYPT_ENCRYPTOR) StringEncryptor stringEncryptor) {
            return new JasyptConfigurationDecryptor(stringEncryptor);
        }

        @Bean(name = BEAN_CM_JASYPT_ENCRYPTOR)
        @ConditionalOnMissingBean(name = BEAN_CM_JASYPT_ENCRYPTOR)
        public StringEncryptor stringEncryptor(
                @Value("${config-manager.encryption.secret-key}") String secretKey) {
            final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            final SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword(secretKey);
            config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
            config.setKeyObtentionIterations(StandardPBEByteEncryptor.DEFAULT_KEY_OBTENTION_ITERATIONS);
            config.setPoolSize(POOL_SIZE);
            config.setProviderName(PROVIDER_NAME);
            config.setSaltGeneratorClassName(SALT_GENERATOR_CLASS_NAME);
            config.setStringOutputType(StandardPBEStringEncryptor.DEFAULT_STRING_OUTPUT_TYPE);
            encryptor.setConfig(config);
            return encryptor;
        }
    }
}
