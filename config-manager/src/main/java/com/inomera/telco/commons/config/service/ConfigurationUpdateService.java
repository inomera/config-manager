package com.inomera.telco.commons.config.service;

import java.util.Map;

/**
 * This is a service to update configurations or insert new ones.
 * Totally optional. You may not need to update configurations programmatically.
 *
 * @author Serdar Kuzucu
 */
public interface ConfigurationUpdateService {
	void updateConfigurations(Map<String, Object> configurationsToUpdate);

	void updateConcatConfiguration(String prefix, String value);
}
