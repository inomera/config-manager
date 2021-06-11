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

    Double getDoubleProperty(String key);

    double getDoubleProperty(String key, double defaultValue);

    Float getFloatProperty(String key);

    float getFloatProperty(String key, float defaultValue);

    Set<String> getStringSetProperty(String key, Set<String> defaultValue);

    Set<String> getStringSetProperty(String key);

    String getConcatStringProperty(String prefix);

    <T> T getJsonObjectProperty(String key, Class<T> classToDeserialize);

    <T> T getJsonObjectProperty(String key, Class<T> classToDeserialize, T defaultValue);

    <T> List<T> getJsonListProperty(String key, Class<T> classToDeserialize);

    <T> List<T> getJsonListProperty(String key, Class<T> classToDeserialize, List<T> defaultValue);

    <T> Set<T> getJsonSetProperty(String key, Class<T> classToDeserialize);

    <T> Set<T> getJsonSetProperty(String key, Class<T> classToDeserialize, Set<T> defaultValue);

    Properties getJavaProperties(String key);

    <T extends Enum<T>> T getEnumValue(String key, Class<T> enumClass);

    <T extends Enum<T>> T getEnumValue(String key, Class<T> enumClass, T defaultValue);

    <T extends Enum<T>> List<T> getEnumList(String key, Class<T> enumClass);

    <T extends Enum<T>> List<T> getEnumList(String key, Class<T> enumClass, List<T> defaultValue);

    <T extends Enum<T>> Set<T> getEnumSet(String key, Class<T> enumClass);

    <T extends Enum<T>> Set<T> getEnumSet(String key, Class<T> enumClass, Set<T> defaultValue);

    void reloadConfigurations();

    String addOnChangeListener(String configKey, ConfigurationChangeListener listener);

    ConfigurationChangeListener removeOnChangeListener(String listenerId);
}
