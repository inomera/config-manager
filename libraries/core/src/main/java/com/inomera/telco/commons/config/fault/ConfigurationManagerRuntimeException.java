package com.inomera.telco.commons.config.fault;

/**
 * @author Serdar Kuzucu
 */
public class ConfigurationManagerRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConfigurationManagerRuntimeException(String message) {
        super(message);
    }

    public ConfigurationManagerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationManagerRuntimeException(Throwable cause) {
        super(cause);
    }
}
