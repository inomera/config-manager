package com.inomera.telco.commons.config;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Serdar Kuzucu
 */
public interface ConfigurationHolder {
	String getStringProperty(String key);

	String getStringProperty(String key, String defaultValue);

	Long getLongProperty(String key);

	long getLongProperty(String key, long defaultValue);

	Integer getIntegerProperty(String key);

	int getIntegerProperty(String key, int defaultValue);

	Boolean getBooleanProperty(String key);

	boolean getBooleanProperty(String key, boolean defaultValue);

	Set<String> getStringSetProperty(String key, Set<String> defaultValue);

	Set<String> getStringSetProperty(String key);

	String getConcatStringProperty(String prefix);

	<T> T getJsonObjectProperty(String key, Class<T> classToDeserialize);

	<T> T getJsonObjectProperty(String key, Class<T> classToDeserialize, T defaultValue);

	<T> List<T> getJsonListProperty(String key, Class<T> classToDeserialize);

	<T> List<T> getJsonListProperty(String key, Class<T> classToDeserialize, List<T> defaultValue);

	Properties getJavaProperties(String key);

	void reloadConfigurations();
}
