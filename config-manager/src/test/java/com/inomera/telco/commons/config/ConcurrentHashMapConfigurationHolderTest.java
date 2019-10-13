package com.inomera.telco.commons.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inomera.telco.commons.config.service.ConfigurationFetcherService;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
class ConcurrentHashMapConfigurationHolderTest {

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

		configurationsMap.put("nonNumericValue", "{#dqw/xyz");

		configurationsMap.put("boolPropTrue", "true");
		configurationsMap.put("boolPropFalse", "false");
		configurationsMap.put("nonBooleanValue", "YES");

		configurationsMap.put("stringSetProp", "a,b,c");
		configurationsMap.put("emptyStringSetProp", "");

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
		assertEquals(new Integer(123), value);
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
		assertFalse(value);
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
		assertFalse(value);
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
		assertFalse(value);
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

		String getName() {
			return name;
		}

		void setName(String name) {
			this.name = name;
		}
	}
}