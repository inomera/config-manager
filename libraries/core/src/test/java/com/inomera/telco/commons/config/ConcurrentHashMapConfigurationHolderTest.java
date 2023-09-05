package com.inomera.telco.commons.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.inomera.telco.commons.config.service.ConfigurationFetcherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
class ConcurrentHashMapConfigurationHolderTest {
    private static final double DELTA = 0.000000001;

    @InjectMocks
    private ConcurrentHashMapConfigurationHolder configurationHolder;

    @Mock
    private ConfigurationFetcherService configurationFetcherService;

    @BeforeEach
    void initialize() {
        final Map<String, String> configurationsMap = new HashMap<>();
        configurationsMap.put("stringProp", "test");

        configurationsMap.put("longProp", "2345");
        configurationsMap.put("intProp", "123");
        configurationsMap.put("doubleProp", "9.99");

        configurationsMap.put("nonNumericValue", "{#dqw/xyz");

        configurationsMap.put("boolPropTrue", "true");
        configurationsMap.put("boolPropFalse", "false");
        configurationsMap.put("nonBooleanValue", "YES");

        configurationsMap.put("stringSetProp", "a,b,c");
        configurationsMap.put("emptyStringSetProp", "");

        configurationsMap.put("illegal-time-unit", "ILLEGAL-ARG");
        configurationsMap.put("time-unit", "SECONDS");
        configurationsMap.put("time-units", "SECONDS,MINUTES");
        configurationsMap.put("time-units-with-illegal", "SECONDS,ILLEGAL-ARG,MINUTES");

        configurationsMap.put("concat.setting.0", "in");
        configurationsMap.put("concat.setting.1", "ter");
        configurationsMap.put("concat.setting.2", "nationalization");

        configurationsMap.put("json.object", "{\"name\": \"Inomera\"}");
        configurationsMap.put("json.array", "[{\"name\": \"Inomera\"}]");
        configurationsMap.put("json.invalid", "12345678_test");
        configurationsMap.put("json.object.null", "null");

        configurationsMap.put("javaPropertyKey", "propertyOne=valueOne\npropertyTwo=valueTwo");

        when(configurationFetcherService.fetchConfiguration()).thenReturn(configurationsMap);
        configurationHolder.reloadConfigurations();
    }

    @Test
    @DisplayName("getStringProperty should return string property")
    void getStringProperty_success() {
        final String value = configurationHolder.getStringProperty("stringProp");
        assertNotNull(value);
        assertEquals("test", value);
    }

    @Test
    @DisplayName("getStringProperty should return null if value not exists in map")
    void getStringProperty_null() {
        final String value = configurationHolder.getStringProperty("stringPropNothing");
        assertNull(value);
    }

    @Test
    @DisplayName("getStringProperty should return string property when default is provided")
    void getStringProperty_defaultSuccess() {
        final String value = configurationHolder.getStringProperty("stringProp", "test1");
        assertNotNull(value);
        assertEquals("test", value);
    }

    @Test
    @DisplayName("getStringProperty should return default value when value not exists")
    void getStringProperty_defaultFallback() {
        final String value = configurationHolder.getStringProperty("stringPropNothing", "defaultValue1");
        assertNotNull(value);
        assertEquals("defaultValue1", value);
    }

    @Test
    @DisplayName("getStringProperty should return null value when value not exists and default is null")
    void getStringProperty_defaultIsNull() {
        final String value = configurationHolder.getStringProperty("stringPropABC", null);
        assertNull(value);
    }

    @Test
    @DisplayName("getConcatStringProperty should return prefixed values as concatenated string")
    void getConcatStringProperty_shouldReturnConcatenatedString() {
        final String value = configurationHolder.getConcatStringProperty("concat.setting");
        assertEquals("internationalization", value);
    }

    @Test
    @DisplayName("getLongProperty should return value as long")
    void getLongProperty_success() {
        final Long value = configurationHolder.getLongProperty("longProp");
        assertNotNull(value);
        assertEquals(2345L, value.longValue());
    }

    @Test
    @DisplayName("getLongProperty should return null when value not exists")
    void getLongProperty_null() {
        final Long value = configurationHolder.getLongProperty("longPropNothing");
        assertNull(value);
    }

    @Test
    @DisplayName("getLongProperty should return value as long when value exists and default value is provided")
    void getLongProperty_defaultSuccess() {
        final long value = configurationHolder.getLongProperty("longProp", 12L);
        assertEquals(2345L, value);
    }

    @Test
    @DisplayName("getLongProperty should return default value when value not exists and default value is provided")
    void getLongProperty_defaultFallback() {
        final long value = configurationHolder.getLongProperty("longPropNothing", 123L);
        assertEquals(123L, value);
    }

    @Test
    @DisplayName("getLongProperty should return null when value cannot be parsed to long")
    void getLongProperty_nullForException() {
        final Long value = configurationHolder.getLongProperty("nonNumericValue");
        assertNull(value);
    }

    @Test
    @DisplayName("getLongProperty should return default value when value cannot be parsed to long")
    void getLongProperty_defaultFallbackWhenException() {
        final long value = configurationHolder.getLongProperty("nonNumericValue", 123L);
        assertEquals(123L, value);
    }

    @Test
    @DisplayName("getIntegerProperty should return value as Integer")
    void getIntegerProperty_success() {
        final Integer value = configurationHolder.getIntegerProperty("intProp");
        assertNotNull(value);
        assertEquals(Integer.valueOf(123), value);
    }

    @Test
    @DisplayName("getIntegerProperty should return null when value not exists in map")
    void getIntegerProperty_null() {
        final Integer value = configurationHolder.getIntegerProperty("intPropNothing");
        assertNull(value);
    }

    @Test
    @DisplayName("getIntegerProperty should return value when value exists in map and default value is provided")
    void getIntegerProperty_defaultSuccess() {
        final int value = configurationHolder.getIntegerProperty("intProp", 12);
        assertEquals(123, value);
    }

    @Test
    @DisplayName("getIntegerProperty should return default value when value not exists in map and default value is provided")
    void getIntegerProperty_defaultFallback() {
        final int value = configurationHolder.getIntegerProperty("intPropNo", 12345);
        assertEquals(12345, value);
    }

    @Test
    @DisplayName("getIntegerProperty should return null when value cannot be parsed to int")
    void getIntegerProperty_nullForException() {
        final Integer value = configurationHolder.getIntegerProperty("nonNumericValue");
        assertNull(value);
    }

    @Test
    @DisplayName("getIntegerProperty should return default value when value cannot be parsed to int")
    void getIntegerProperty_defaultFallbackWhenException() {
        final int value = configurationHolder.getIntegerProperty("nonNumericValue", 12345);
        assertEquals(12345, value);
    }

    @Test
    @DisplayName("getBooleanProperty should return true when value in map is true")
    void getBooleanProperty_success_TRUE() {
        final Boolean value = configurationHolder.getBooleanProperty("boolPropTrue");
        assertNotNull(value);
        assertTrue(value);
    }

    @Test
    @DisplayName("getBooleanProperty should return false when value in map is false")
    void getBooleanProperty_success_FALSE() {
        final Boolean value = configurationHolder.getBooleanProperty("boolPropFalse");
        assertNotNull(value);
        Assertions.assertFalse(value);
    }

    @Test
    @DisplayName("getBooleanProperty should return null when value in map is null")
    void getBooleanProperty_null() {
        final Boolean value = configurationHolder.getBooleanProperty("boolPropNothing");
        assertNull(value);
    }

    @Test
    @DisplayName("getBooleanProperty should return value in map when it exists and default value is provided")
    void getBooleanProperty_defaultSuccess() {
        final boolean value = configurationHolder.getBooleanProperty("boolPropTrue", false);
        assertTrue(value);
    }

    @Test
    @DisplayName("getBooleanProperty should return false when value doesn't exist and default value is false")
    void getBooleanProperty_defaultFallback_FALSE() {
        final boolean value = configurationHolder.getBooleanProperty("boolPropNothing", false);
        Assertions.assertFalse(value);
    }

    @Test
    @DisplayName("getBooleanProperty should return true when value doesn't exist and default value is true")
    void getBooleanProperty_defaultFallback_TRUE() {
        final boolean value = configurationHolder.getBooleanProperty("boolPropNothing", true);
        assertTrue(value);
    }

    @Test
    @DisplayName("getBooleanProperty should return null when value cannot be parsed to boolean")
    void getBooleanProperty_nullForException() {
        final Boolean value = configurationHolder.getBooleanProperty("nonBooleanValue");
        assertNull(value);
    }

    @Test
    @DisplayName("getBooleanProperty should return true when value cannot be parsed to boolean and default is true")
    void getBooleanProperty_defaultFallbackWhenException_true() {
        final boolean value = configurationHolder.getBooleanProperty("nonBooleanValue", true);
        assertTrue(value);
    }

    @Test
    @DisplayName("getBooleanProperty should return false when value cannot be parsed to boolean and default is false")
    void getBooleanProperty_defaultFallbackWhenException_false() {
        final boolean value = configurationHolder.getBooleanProperty("nonBooleanValue", false);
        Assertions.assertFalse(value);
    }

    @Test
    @DisplayName("getStringSetProperty should return value as set when default not provided")
    void getStringSetProperty_successWithoutDefault() {
        final Set<String> expected = new HashSet<>(Arrays.asList("a", "b", "c"));

        final Set<String> value = configurationHolder.getStringSetProperty("stringSetProp");
        assertNotNull(value);
        assertEquals(expected, value);
    }

    @Test
    @DisplayName("getStringSetProperty should return value as set when default is provided")
    void getStringSetProperty_successWithDefault() {
        final Set<String> expected = new HashSet<>(Arrays.asList("a", "b", "c"));
        final Set<String> defaultValue = new HashSet<>(Arrays.asList("d", "e"));

        final Set<String> value = configurationHolder.getStringSetProperty("stringSetProp", defaultValue);
        assertNotNull(value);
        assertEquals(expected, value);
    }

    @Test
    @DisplayName("getStringSetProperty should return empty set when value not exists and default not provided")
    void getStringSetProperty_null() {
        final Set<String> value = configurationHolder.getStringSetProperty("stringSetPropNull");
        assertNotNull(value);
        assertEquals(0, value.size());
    }

    @Test
    @DisplayName("getStringSetProperty should return null when value not exists and default is null")
    void getStringSetProperty_nullValueAndNullDefault() {
        final Set<String> value = configurationHolder.getStringSetProperty("stringSetPropNull", null);
        assertNull(value);
    }

    @Test
    @DisplayName("getStringSetProperty should return default value when value is empty string")
    void getStringSetProperty_emptyValueReturnsDefault() {
        final Set<String> expected = new HashSet<>(Arrays.asList("a", "b", "c"));

        final Set<String> value = configurationHolder.getStringSetProperty("emptyStringSetProp", expected);
        assertNotNull(value);
        assertEquals(expected, value);
    }

    @Test
    @DisplayName("getStringSetProperty should return default value when value not exist in map")
    void getStringSetProperty_nullValueReturnsDefault() {
        final Set<String> expected = new HashSet<>(Arrays.asList("a", "b", "c"));

        final Set<String> value = configurationHolder.getStringSetProperty("stringSetNothing", expected);
        assertNotNull(value);
        assertEquals(expected, value);
    }

    @Test
    @DisplayName("getJsonObjectProperty should return null when property is not found")
    void getJsonObjectProperty_shouldReturnNullWhenPropertyNotExist() {
        final JsonObject value = configurationHolder.getJsonObjectProperty("jsonPropNothing", JsonObject.class);
        assertNull(value);
    }

    @Test
    @DisplayName("getJsonObjectProperty should return null when property is not a valid json object")
    void getJsonObjectProperty_shouldReturnNullWhenInvalidJsonObjectInMap() {
        final JsonObject value = configurationHolder.getJsonObjectProperty("json.invalid", JsonObject.class);
        assertNull(value);
    }

    @Test
    @DisplayName("getJsonObjectProperty should return instance of given class when property valid json object")
    void getJsonObjectProperty_shouldReturnInstanceOfGivenClassWhenValidJsonProvided() {
        final JsonObject value = configurationHolder.getJsonObjectProperty("json.object", JsonObject.class);
        assertNotNull(value);
        assertEquals("Inomera", value.getName());
    }

    @Test
    @DisplayName("getJsonObjectProperty should return default value when property is not found")
    void getJsonObjectProperty_shouldReturnDefaultValueWhenPropertyNotExist() {
        final JsonObject defaultValue = new JsonObject();
        defaultValue.setName("Test");

        final JsonObject value = configurationHolder.getJsonObjectProperty("jsonPropNothing", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertSame(defaultValue, value);
        assertEquals("Test", value.getName());
    }

    @Test
    @DisplayName("getJsonObjectProperty should return default value when property is not a valid json object")
    void getJsonObjectProperty_shouldReturnDefaultValueWhenInvalidJsonObjectInMap() {
        final JsonObject defaultValue = new JsonObject();
        defaultValue.setName("Test1");

        final JsonObject value = configurationHolder.getJsonObjectProperty("json.invalid", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertSame(defaultValue, value);
        assertEquals("Test1", value.getName());
    }

    @Test
    @DisplayName("getJsonObjectProperty should return instance of given class when property valid json object and default value is provided")
    void getJsonObjectProperty_shouldReturnInstanceOfGivenClassWhenValidJsonProvidedAndDefaultIsProvided() {
        final JsonObject defaultValue = new JsonObject();
        defaultValue.setName("Test1");

        final JsonObject value = configurationHolder.getJsonObjectProperty("json.object", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertEquals("Inomera", value.getName());
        assertNotSame(defaultValue, value);
    }

    @Test
    @DisplayName("getJsonObjectProperty should return default value when property value is null string")
    void getJsonObjectProperty_shouldReturnDefaultValueWhenPropertyValueIsNullString() {
        final JsonObject defaultValue = new JsonObject();
        defaultValue.setName("Test1");

        final JsonObject value = configurationHolder.getJsonObjectProperty("json.object.null", JsonObject.class, defaultValue);
        assertSame(value, defaultValue);
    }

    @Test
    @DisplayName("getJsonListProperty should return empty list when property is not found")
    void getJsonListProperty_shouldReturnEmptyListWhenPropertyNotExist() {
        final List<JsonObject> value = configurationHolder.getJsonListProperty("jsonPropNothing", JsonObject.class);
        assertNotNull(value);
        assertEquals(0, value.size());
    }

    @Test
    @DisplayName("getJsonListProperty should return empty list when property is not a valid json array")
    void getJsonListProperty_shouldReturnEmptyListWhenInvalidJsonObjectInMap() {
        final List<JsonObject> value = configurationHolder.getJsonListProperty("json.invalid", JsonObject.class);
        assertNotNull(value);
        assertEquals(0, value.size());
    }

    @Test
    @DisplayName("getJsonListProperty should return list of instance of given class when property valid json array")
    void getJsonListProperty_shouldReturnInstanceOfGivenClassWhenValidJsonProvided() {
        final List<JsonObject> value = configurationHolder.getJsonListProperty("json.array", JsonObject.class);
        assertNotNull(value);
        assertEquals(1, value.size());
        assertEquals("Inomera", value.get(0).getName());
    }

    @Test
    @DisplayName("getJsonListProperty should return default value when property is not found")
    void getJsonListProperty_shouldReturnDefaultValueWhenPropertyNotExist() {
        final JsonObject defaultValueItem = new JsonObject();
        defaultValueItem.setName("Test");
        final List<JsonObject> defaultValue = new ArrayList<>();
        defaultValue.add(defaultValueItem);

        final List<JsonObject> value = configurationHolder.getJsonListProperty("jsonPropNothing", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertSame(defaultValue, value);
        assertEquals(1, value.size());
        assertEquals("Test", value.get(0).getName());
    }

    @Test
    @DisplayName("getJsonListProperty should return default value when property is not a valid json array")
    void getJsonListProperty_shouldReturnDefaultValueWhenInvalidJsonObjectInMap() {
        final JsonObject defaultValueItem = new JsonObject();
        defaultValueItem.setName("Test");
        final List<JsonObject> defaultValue = new ArrayList<>();
        defaultValue.add(defaultValueItem);

        final List<JsonObject> value = configurationHolder.getJsonListProperty("json.invalid", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertSame(defaultValue, value);
        assertEquals(1, value.size());
        assertEquals("Test", value.get(0).getName());
    }

    @Test
    @DisplayName("getJsonListProperty should return list of instance of given class when property valid json array and default value is provided")
    void getJsonListProperty_shouldReturnInstanceOfGivenClassWhenValidJsonProvidedAndDefaultIsProvided() {
        final JsonObject defaultValueItem = new JsonObject();
        defaultValueItem.setName("Test");
        final List<JsonObject> defaultValue = new ArrayList<>();
        defaultValue.add(defaultValueItem);

        final List<JsonObject> value = configurationHolder.getJsonListProperty("json.array", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertEquals(1, value.size());
        assertEquals("Inomera", value.get(0).getName());
        assertNotSame(defaultValue, value);
    }

    @Test
    @DisplayName("getJavaProperties should load java properties from string value")
    void getJavaProperties_shouldGetJavaProperties() {
        final Properties properties = configurationHolder.getJavaProperties("javaPropertyKey");

        assertNotNull(properties);
        assertEquals("valueOne", properties.getProperty("propertyOne"));
        assertEquals("valueTwo", properties.getProperty("propertyTwo"));
    }

    @Test
    @DisplayName("getJsonSetProperty should return empty set when property is not found")
    void getJsonSetProperty_shouldReturnEmptyListWhenPropertyNotExist() {
        final Set<JsonObject> value = configurationHolder.getJsonSetProperty("jsonPropNothing", JsonObject.class);
        assertNotNull(value);
        assertEquals(0, value.size());
    }

    @Test
    @DisplayName("getJsonSetProperty should return empty set when property is not a valid json array")
    void getJsonSetProperty_shouldReturnEmptyListWhenInvalidJsonObjectInMap() {
        final Set<JsonObject> value = configurationHolder.getJsonSetProperty("json.invalid", JsonObject.class);
        assertNotNull(value);
        assertEquals(0, value.size());
    }

    @Test
    @DisplayName("getJsonSetProperty should return set of instance of given class when property valid json array")
    void getJsonSetProperty_shouldReturnInstanceOfGivenClassWhenValidJsonProvided() {
        final Set<JsonObject> value = configurationHolder.getJsonSetProperty("json.array", JsonObject.class);
        assertNotNull(value);
        assertEquals(1, value.size());
        assertTrue(value.contains(new JsonObject("Inomera")), "Returned set should contain word 'Inomera'");
    }

    @Test
    @DisplayName("getJsonSetProperty should return default value when property is not found")
    void getJsonSetProperty_shouldReturnDefaultValueWhenPropertyNotExist() {
        final JsonObject defaultValueItem = new JsonObject();
        defaultValueItem.setName("Test");
        final Set<JsonObject> defaultValue = new HashSet<>();
        defaultValue.add(defaultValueItem);

        final Set<JsonObject> value = configurationHolder.getJsonSetProperty("jsonPropNothing", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertSame(defaultValue, value);
        assertEquals(1, value.size());
        assertTrue(value.contains(new JsonObject("Test")), "Returned set should contain word 'Test'");
    }

    @Test
    @DisplayName("getJsonSetProperty should return default value when property is not a valid json array")
    void getJsonSetProperty_shouldReturnDefaultValueWhenInvalidJsonObjectInMap() {
        final JsonObject defaultValueItem = new JsonObject();
        defaultValueItem.setName("Test");
        final Set<JsonObject> defaultValue = new HashSet<>();
        defaultValue.add(defaultValueItem);

        final Set<JsonObject> value = configurationHolder.getJsonSetProperty("json.invalid", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertSame(defaultValue, value);
        assertEquals(1, value.size());
        assertTrue(value.contains(new JsonObject("Test")), "Returned set should contain word 'Test'");
    }

    @Test
    @DisplayName("getJsonSetProperty should return set of instance of given class when property valid json array and default value is provided")
    void getJsonSetProperty_shouldReturnInstanceOfGivenClassWhenValidJsonProvidedAndDefaultIsProvided() {
        final JsonObject defaultValueItem = new JsonObject();
        defaultValueItem.setName("Test");
        final Set<JsonObject> defaultValue = new HashSet<>();
        defaultValue.add(defaultValueItem);

        final Set<JsonObject> value = configurationHolder.getJsonSetProperty("json.array", JsonObject.class, defaultValue);
        assertNotNull(value);
        assertEquals(1, value.size());
        assertTrue(value.contains(new JsonObject("Inomera")), "Returned set should contain word 'Inomera'");
        assertNotSame(defaultValue, value);
    }

    @Test
    @DisplayName("getDoubleProperty should return value as Double")
    void getDoubleProperty_success() {
        final Double value = configurationHolder.getDoubleProperty("doubleProp");
        assertNotNull(value);
        assertEquals(9.99, value, DELTA);
    }

    @Test
    @DisplayName("getDoubleProperty should return null when value not exists in map")
    void getDoubleProperty_null() {
        final Double value = configurationHolder.getDoubleProperty("doublePropNothing");
        assertNull(value);
    }

    @Test
    @DisplayName("getDoubleProperty should return value when value exists in map and default value is provided")
    void getDoubleProperty_defaultSuccess() {
        final double value = configurationHolder.getDoubleProperty("doubleProp", 12.9);
        assertEquals(9.99, value, DELTA);
    }

    @Test
    @DisplayName("getDoubleProperty should return default value when value not exists in map and default value is provided")
    void getDoubleProperty_defaultFallback() {
        final double value = configurationHolder.getDoubleProperty("doublePropNo", 123.45);
        assertEquals(123.45, value, DELTA);
    }

    @Test
    @DisplayName("getDoubleProperty should return null when value cannot be parsed to double")
    void getDoubleProperty_nullForException() {
        final Double value = configurationHolder.getDoubleProperty("nonNumericValue");
        assertNull(value);
    }

    @Test
    @DisplayName("getDoubleProperty should return default value when value cannot be parsed to double")
    void getDoubleProperty_defaultFallbackWhenException() {
        final double value = configurationHolder.getDoubleProperty("nonNumericValue", 123.45);
        assertEquals(123.45, value, DELTA);
    }

    @Test
    @DisplayName("getFloatProperty should return value as Float")
    void getFloatProperty_success() {
        final Float value = configurationHolder.getFloatProperty("doubleProp");
        assertNotNull(value);
        assertEquals(9.99f, value, DELTA);
    }

    @Test
    @DisplayName("getFloatProperty should return null when value not exists in map")
    void getFloatProperty_null() {
        final Float value = configurationHolder.getFloatProperty("doublePropNothing");
        assertNull(value);
    }

    @Test
    @DisplayName("getFloatProperty should return value when value exists in map and default value is provided")
    void getFloatProperty_defaultSuccess() {
        final float value = configurationHolder.getFloatProperty("doubleProp", 12.9f);
        assertEquals(9.99f, value, DELTA);
    }

    @Test
    @DisplayName("getFloatProperty should return default value when value not exists in map and default value is provided")
    void getFloatProperty_defaultFallback() {
        final float value = configurationHolder.getFloatProperty("doublePropNo", 123.45f);
        assertEquals(123.45f, value, DELTA);
    }

    @Test
    @DisplayName("getFloatProperty should return null when value cannot be parsed to float")
    void getFloatProperty_nullForException() {
        final Float value = configurationHolder.getFloatProperty("nonNumericValue");
        assertNull(value);
    }

    @Test
    @DisplayName("getFloatProperty should return default value when value cannot be parsed to float")
    void getFloatProperty_defaultFallbackWhenException() {
        final float value = configurationHolder.getFloatProperty("nonNumericValue", 123.45f);
        assertEquals(123.45f, value, DELTA);
    }

    @Test
    @DisplayName("getEnumValue should return null when property is not found")
    void getEnumValue_shouldReturnNullWhenPropertyNotExist() {
        final TimeUnit value = configurationHolder.getEnumValue("time-unit-not-exist", TimeUnit.class);
        assertNull(value);
    }

    @Test
    @DisplayName("getEnumValue should return null when property is not a valid enum value")
    void getEnumValue_shouldReturnNullWhenInvalidEnumValueInMap() {
        final TimeUnit value = configurationHolder.getEnumValue("illegal-time-unit", TimeUnit.class);
        assertNull(value);
    }

    @Test
    @DisplayName("getEnumValue should return instance of given class when property is valid enum value")
    void getEnumValue_shouldReturnInstanceOfGivenClassWhenValidEnumValue() {
        final TimeUnit value = configurationHolder.getEnumValue("time-unit", TimeUnit.class);
        assertSame(TimeUnit.SECONDS, value);
    }

    @Test
    @DisplayName("getEnumValue should return default value when property is not found")
    void getEnumValue_shouldReturnDefaultValueWhenPropertyNotExist() {
        final TimeUnit value = configurationHolder.getEnumValue("time-unit-not-exist", TimeUnit.class, TimeUnit.NANOSECONDS);
        assertSame(TimeUnit.NANOSECONDS, value);
    }

    @Test
    @DisplayName("getEnumValue should return default value when property is not a valid enum value")
    void getEnumValue_shouldReturnDefaultValueWhenInvalidEnumValueInMap() {
        final TimeUnit value = configurationHolder.getEnumValue("illegal-time-unit", TimeUnit.class, TimeUnit.DAYS);
        assertSame(TimeUnit.DAYS, value);
    }

    @Test
    @DisplayName("getEnumValue should return instance of given class when property is a valid enum value and default value is provided")
    void getEnumValue_shouldReturnInstanceOfGivenClassWhenValidEnumValueProvidedAndDefaultIsProvided() {
        final TimeUnit value = configurationHolder.getEnumValue("time-unit", TimeUnit.class, TimeUnit.DAYS);
        assertSame(TimeUnit.SECONDS, value);
    }

    @Test
    @DisplayName("getEnumSet should return empty set when property is not found")
    void getEnumSet_shouldReturnEmptySetWhenPropertyNotExist() {
        final Set<TimeUnit> value = configurationHolder.getEnumSet("time-units-not-exist", TimeUnit.class);
        assertTrue(value.isEmpty(), "Set should be empty");
    }

    @Test
    @DisplayName("getEnumSet should return set of enums")
    void getEnumSet_shouldReturnSetOfEnums() {
        final Set<TimeUnit> value = configurationHolder.getEnumSet("time-units", TimeUnit.class);
        assertEquals(Sets.newHashSet(TimeUnit.SECONDS, TimeUnit.MINUTES), value);
    }

    @Test
    @DisplayName("getEnumSet should skip illegal enum values")
    void getEnumSet_shouldSkipIllegalEnumValues() {
        final Set<TimeUnit> value = configurationHolder.getEnumSet("time-units-with-illegal", TimeUnit.class);
        assertEquals(Sets.newHashSet(TimeUnit.SECONDS, TimeUnit.MINUTES), value);
    }

    @Test
    @DisplayName("getEnumSet should return default value when property is not found")
    void getEnumSet_shouldReturnDefaultValueWhenPropertyNotExist() {
        final Set<TimeUnit> value = configurationHolder.getEnumSet("time-units-not-exist", TimeUnit.class,
                Sets.newHashSet(TimeUnit.MILLISECONDS));
        assertEquals(Sets.newHashSet(TimeUnit.MILLISECONDS), value);
    }

    @Test
    @DisplayName("getEnumSet should return empty set when property is not a valid enum value and default is provided")
    void getEnumSet_shouldReturnEmptySetWhenDefaultIsProvidedAndPropertyIsIllegal() {
        final Set<TimeUnit> value = configurationHolder.getEnumSet("illegal-time-unit", TimeUnit.class,
                Sets.newHashSet(TimeUnit.MILLISECONDS));
        assertTrue(value.isEmpty(), "Set should be empty");
    }

    @Test
    @DisplayName("getEnumSet should return instance of given class when property is a valid enum value and default value is provided")
    void getEnumSet_shouldReturnInstanceOfGivenClassWhenValidEnumValueProvidedAndDefaultIsProvided() {
        final Set<TimeUnit> value = configurationHolder.getEnumSet("time-units", TimeUnit.class,
                Sets.newHashSet(TimeUnit.MILLISECONDS));
        assertEquals(Sets.newHashSet(TimeUnit.SECONDS, TimeUnit.MINUTES), value);
    }

    @Test
    @DisplayName("getEnumList should return empty list when property is not found")
    void getEnumList_shouldReturnEmptyListWhenPropertyNotExist() {
        final List<TimeUnit> value = configurationHolder.getEnumList("time-units-not-exist", TimeUnit.class);
        assertTrue(value.isEmpty(), "List should be empty");
    }

    @Test
    @DisplayName("getEnumList should return list of enums")
    void getEnumList_shouldReturnListOfEnums() {
        final List<TimeUnit> value = configurationHolder.getEnumList("time-units", TimeUnit.class);
        assertEquals(Lists.newArrayList(TimeUnit.SECONDS, TimeUnit.MINUTES), value);
    }

    @Test
    @DisplayName("getEnumList should skip illegal enum values")
    void getEnumList_shouldSkipIllegalEnumValues() {
        final List<TimeUnit> value = configurationHolder.getEnumList("time-units-with-illegal", TimeUnit.class);
        assertEquals(Lists.newArrayList(TimeUnit.SECONDS, TimeUnit.MINUTES), value);
    }

    @Test
    @DisplayName("getEnumList should return default value when property is not found")
    void getEnumList_shouldReturnDefaultValueWhenPropertyNotExist() {
        final List<TimeUnit> value = configurationHolder.getEnumList("time-units-not-exist", TimeUnit.class,
                Lists.newArrayList(TimeUnit.MILLISECONDS));
        assertEquals(Lists.newArrayList(TimeUnit.MILLISECONDS), value);
    }

    @Test
    @DisplayName("getEnumList should return empty list when property is not a valid enum value and default is provided")
    void getEnumList_shouldReturnEmptySetWhenDefaultIsProvidedAndPropertyIsIllegal() {
        final List<TimeUnit> value = configurationHolder.getEnumList("illegal-time-unit", TimeUnit.class,
                Lists.newArrayList(TimeUnit.MILLISECONDS));
        assertTrue(value.isEmpty(), "List should be empty");
    }

    @Test
    @DisplayName("getEnumList should return instance of given class when property is a valid enum value and default value is provided")
    void getEnumList_shouldReturnInstanceOfGivenClassWhenValidEnumValueProvidedAndDefaultIsProvided() {
        final List<TimeUnit> value = configurationHolder.getEnumList("time-units", TimeUnit.class,
                Lists.newArrayList(TimeUnit.MILLISECONDS));
        assertEquals(Lists.newArrayList(TimeUnit.SECONDS, TimeUnit.MINUTES), value);
    }

    @Test
    @DisplayName("add On Change Listener for Configuration changes")
    void addOnChangeListener() {
        final ConfigurationChangeListener listener = (configKey, oldValue, newValue) -> {

        };
        final String listenerId = configurationHolder.addOnChangeListener("configKey", listener);
        final ConfigurationChangeListener removedListener = configurationHolder.removeOnChangeListener(listenerId);
        assertSame(listener, removedListener);
    }

    @Test
    @DisplayName("remove non existent listener for configurations")
    void removeNonExistentListenerForConfigurations() {
        final String listenerId = UUID.randomUUID().toString();
        final ConfigurationChangeListener removedListener = configurationHolder.removeOnChangeListener(listenerId);
        assertNull(removedListener);
    }

    @Test
    @DisplayName("remove On Change Listener in different order")
    void removeOnChangeListenerInDifferentOrder() {
        ConfigurationChangeListener listener1 = (configKey, oldValue, newValue) -> {

        };
        ConfigurationChangeListener listener2 = (configKey, oldValue, newValue) -> {

        };
        ConfigurationChangeListener listener3 = (configKey, oldValue, newValue) -> {
        };

        final String listenerId1 = configurationHolder.addOnChangeListener("configKey", listener1);
        final String listenerId2 = configurationHolder.addOnChangeListener("configKey", listener2);
        final String listenerId3 = configurationHolder.addOnChangeListener("configKey", listener3);

        final ConfigurationChangeListener removedListener2 = configurationHolder.removeOnChangeListener(listenerId2);
        final ConfigurationChangeListener removedListener3 = configurationHolder.removeOnChangeListener(listenerId3);
        final ConfigurationChangeListener removedListener1 = configurationHolder.removeOnChangeListener(listenerId1);

        assertSame(listener1, removedListener1);
        assertSame(listener2, removedListener2);
        assertSame(listener3, removedListener3);
    }

    @Test
    @DisplayName("reloadConfigurations should change detection")
    void reloadConfigurations_shouldChangeDetection() {
        AtomicBoolean isListenerCalled = new AtomicBoolean(false);

        final Map<String, String> configurationsMap = new HashMap<>();
        configurationsMap.put("stringProp", "test1");

        configurationHolder.addOnChangeListener("stringProp", (configKey, oldValue, newValue) -> {
            assertEquals("test1", newValue);
            assertEquals("test", oldValue);
            assertEquals("stringProp", configKey);
            isListenerCalled.set(true);
        });

        when(configurationFetcherService.fetchConfiguration()).thenReturn(configurationsMap);
        configurationHolder.reloadConfigurations();
        assertTrue(isListenerCalled.get());
    }

    @Test
    @DisplayName("reloadConfigurations should load from service")
    void reloadConfigurations_shouldLoadFromService() {
        final Map<String, String> previousMap = new HashMap<>();
        previousMap.put("a", "1");
        previousMap.put("b", "2");
        previousMap.put("c", "3");

        final Map<String, String> nextMap = new HashMap<>();
        nextMap.put("a", "5");
        nextMap.put("b", "6");
        nextMap.put("d", "9");

        when(configurationFetcherService.fetchConfiguration())
                .thenReturn(previousMap)
                .thenReturn(nextMap);

        configurationHolder.reloadConfigurations();

        assertEquals(1, configurationHolder.getIntegerProperty("a", 1453));
        assertEquals(2, configurationHolder.getIntegerProperty("b", 1453));
        assertEquals(3, configurationHolder.getIntegerProperty("c", 1453));
        assertEquals(1453, configurationHolder.getIntegerProperty("d", 1453));

        configurationHolder.reloadConfigurations();

        assertEquals(5, configurationHolder.getIntegerProperty("a", 1453));
        assertEquals(6, configurationHolder.getIntegerProperty("b", 1453));
        assertEquals(1453, configurationHolder.getIntegerProperty("c", 1453));
        assertEquals(9, configurationHolder.getIntegerProperty("d", 1453));
    }

    private static class JsonObject {
        private String name;

        public JsonObject() {
        }

        public JsonObject(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            JsonObject that = (JsonObject) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
