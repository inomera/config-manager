package com.inomera.telco.commons.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.inomera.telco.commons.config.change.ConfigurationChangeListener;
import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import com.inomera.telco.commons.lang.function.ThrowableFunction;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Caches all configuration into a ConcurrentHashMap. Reloads the map every 60 seconds
 *
 * @author Serdar Kuzucu
 */
public class ConcurrentHashMapConfigurationHolder implements ConfigurationHolder {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentHashMapConfigurationHolder.class);
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private Map<String, Map<String, ConfigurationChangeListener>> changeListeners = new ConcurrentHashMap<>();
    private Map<String, String> configurationsLocalMap = new ConcurrentHashMap<>();

    private Lock configurationsLocalMapLock = new ReentrantLock();
    private ConfigurationFetcherService configurationFetcherService;
    private ObjectMapper objectMapper;

    public ConcurrentHashMapConfigurationHolder(ConfigurationFetcherService configurationFetcherService) {
        this.configurationFetcherService = configurationFetcherService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.reloadConfigurations();
    }

    @Override
    public String getStringProperty(String key) {
        return configurationsLocalMap.get(key);
    }

    @Override
    public String getStringProperty(String key, String defaultValue) {
        return convert(key, v -> v, defaultValue);
    }

    @Override
    public String getConcatStringProperty(String prefix) {
        // Hold a reference to the map for thread safety!
        // Because the map may change in the middle of for loop below!
        final Map<String, String> configurationsMap = configurationsLocalMap;
        final StringBuilder sb = new StringBuilder();

        int index = 0;
        String propertyValue = configurationsMap.get(prefix + "." + index);
        while (propertyValue != null) {
            sb.append(propertyValue);
            index++;
            propertyValue = configurationsMap.get(prefix + "." + index);
        }

        return sb.toString();
    }

    @Override
    public Long getLongProperty(String key) {
        return convert(key, Long::valueOf, null);
    }

    @Override
    public long getLongProperty(String key, long defaultValue) {
        return convert(key, Long::valueOf, defaultValue);
    }

    @Override
    public Integer getIntegerProperty(String key) {
        return convert(key, Integer::valueOf, null);
    }

    @Override
    public int getIntegerProperty(String key, int defaultValue) {
        return convert(key, Integer::valueOf, defaultValue);
    }

    @Override
    public Boolean getBooleanProperty(String key) {
        return convert(key, this::toBoolean, null);
    }

    @Override
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return convert(key, this::toBoolean, defaultValue);
    }

    @Override
    public Set<String> getStringSetProperty(String key, Set<String> defaultValue) {
        return convert(key, this::toStringSet, defaultValue);
    }

    @Override
    public Set<String> getStringSetProperty(String key) {
        return convert(key, this::toStringSet, new HashSet<>());
    }

    @Override
    public <T> T getJsonObjectProperty(String key, Class<T> classToDeserialize) {
        return convert(key, toObject(classToDeserialize, objectMapper), null);
    }

    @Override
    public <T> T getJsonObjectProperty(String key, Class<T> classToDeserialize, T defaultValue) {
        return convert(key, toObject(classToDeserialize, objectMapper), defaultValue);
    }

    @Override
    public <T> List<T> getJsonListProperty(String key, Class<T> classToDeserialize, List<T> defaultValue) {
        return convert(key, toList(classToDeserialize, objectMapper), defaultValue);
    }

    @Override
    public <T> List<T> getJsonListProperty(String key, Class<T> classToDeserialize) {
        return convert(key, toList(classToDeserialize, objectMapper), new ArrayList<>());
    }

    @Override
    public Properties getJavaProperties(String key) {
        final ThrowableFunction<String, Properties> converter = value -> {
            Properties properties = new Properties();
            properties.load(new StringReader(value));
            return properties;
        };
        return convert(key, converter, new Properties());
    }

    @Override
    public void reloadConfigurations() {
        try {
            // I do not want two threads to update the map at the same time.
            configurationsLocalMapLock.lock();
            LOG.info("Reloading system properties..");

            final Map<String, String> allConfigurationValues = configurationFetcherService.fetchConfiguration();
            final Map<String, String> tmpConfigurationsMap = new ConcurrentHashMap<>(allConfigurationValues);

            // check for emptiness. db problem might occur. keep current config.
            if (!tmpConfigurationsMap.isEmpty()) {

                final Set<String> oldKeysSet = configurationsLocalMap.keySet();
                Set<String> configKeysSet = new HashSet<>(oldKeysSet);
                final Set<String> newKeysSet = tmpConfigurationsMap.keySet();
                configKeysSet.addAll(newKeysSet);

                configKeysSet.stream().filter(configKey -> !Objects.equals(configurationsLocalMap.get(configKey),
                        tmpConfigurationsMap.get(configKey))).forEach(configKey ->
                        changeListeners.getOrDefault(configKey, Collections.emptyMap()).values().forEach(listener ->
                                listener.onChange(configKey, configurationsLocalMap.get(configKey), tmpConfigurationsMap.get(configKey))));

                configurationsLocalMap = tmpConfigurationsMap;
            }
        } finally {
            configurationsLocalMapLock.unlock();
        }
    }

    @Override
    public String addOnChangeListener(String configKey, ConfigurationChangeListener listener) {
        final String listenerId = UUID.randomUUID().toString();
        changeListeners.computeIfAbsent(configKey, k -> new ConcurrentHashMap<>())
                .put(listenerId, listener);
        return listenerId;
    }

    @Override
    public ConfigurationChangeListener removeOnChangeListener(String listenerId) {
        return changeListeners.values()
                .stream()
                .map(subMap -> subMap.remove(listenerId))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Boolean toBoolean(String value) {
        return BooleanUtils.toBoolean(value, TRUE, FALSE);
    }

    private Set<String> toStringSet(String value) {
        final Set<String> values = new HashSet<>();
        Collections.addAll(values, StringUtils.splitPreserveAllTokens(value, ","));
        return values;
    }

    private static <T> ThrowableFunction<String, T> toObject(Class<T> classToDeserialize, ObjectMapper objectMapper) {
        return value -> objectMapper.readValue(value, classToDeserialize);
    }

    private static <T> ThrowableFunction<String, List<T>> toList(Class<T> classToDeserialize, ObjectMapper objectMapper) {
        final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, classToDeserialize);
        return value -> objectMapper.readValue(value, collectionType);
    }

    private <T> T convert(String key, ThrowableFunction<String, T> converter, T defaultValue) {
        final String value = getStringProperty(key);

        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        try {
            final T convertedValue = converter.apply(value);
            if (convertedValue == null) {
                return defaultValue;
            }
            return convertedValue;
        } catch (Exception e) {
            LOG.error("defaultOnExceptionOrNullValue::key={}::value={}::error={}", key, value, e.getMessage(), e);
        }

        return defaultValue;
    }
}
