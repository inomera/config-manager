package com.inomera.telco.commons.config.service;

import java.util.Map;

/**
 * This is the service which will be used by ConfigurationHolder to fetch configurations
 *
 * @author Serdar Kuzucu
 */
public interface ConfigurationFetcherService {
	Map<String, String> fetchConfiguration();
}
