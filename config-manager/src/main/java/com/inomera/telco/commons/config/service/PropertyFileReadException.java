package com.inomera.telco.commons.config.service;

/**
 * @author Serdar Kuzucu
 */
public class PropertyFileReadException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PropertyFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
