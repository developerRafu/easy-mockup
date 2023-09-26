package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * The Mockup class provides utility methods for creating mock objects with default or custom property values.
 * It allows for the creation of mock objects for testing purposes, making it easier to simulate object behavior
 * without relying on actual implementations.
 */
public final class Mockup {
    private static final Logger logger = LoggerFactory.getLogger(Mockup.class);

    /**
     * Creates a mock object of the specified class.
     *
     * @param <T>        The type of the mock object to be created.
     * @param mockClass  The Class object representing the type of mock object to create.
     * @return           A mock object of the specified class with default values set for its properties.
     *                   Returns null if an error occurs during the mock object creation.
     */
    public static <T> T createMock(Class<T> mockClass) {
        try {
            Constructor<T> constructor = mockClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            final var instance = constructor.newInstance();
            for (Method method : instance.getClass().getDeclaredMethods()) {
                if (isSetter(method)) {
                    if (isOnlyOneParameter(method)) {
                        final var parameter = method.getParameterTypes()[0];
                        method.invoke(instance, getDefaultValue(parameter));
                    }
                }
            }
            return instance;
        } catch (Exception ex) {
            logger.error("Error creating mock", ex);
            return null;
        }
    }

    /**
     * Creates a mock object of the specified class with the option to set custom values for properties.
     *
     * @param <T>        The type of the mock object to be created.
     * @param mockClass  The Class object representing the type of mock object to create.
     * @param values     A map containing custom property values to set on the mock object.
     * @return           A mock object of the specified class with either default values or custom values
     *                   set for its properties as defined in the 'values' map. Returns null if an error
     *                   occurs during the mock object creation.
     */
    public static <T> T createMock(Class<T> mockClass, Map<String, Object> values) {
        try {
            Constructor<T> constructor = mockClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            final var instance = constructor.newInstance();
            for (Method method : instance.getClass().getDeclaredMethods()) {
                if (isSetter(method)) {
                    if (isOnlyOneParameter(method)) {
                        final var parameter = method.getParameterTypes()[0];
                        method.invoke(instance, getDefaultValueOrParameter(parameter, method, values, null));
                    }
                }
            }
            return instance;
        } catch (Exception ex) {
            logger.error("Error creating mock", ex);
            return null;
        }
    }

    /**
     * Creates a mock object of the specified class with the option to set custom values for properties.
     *
     * @param <T>               The type of the mock object to be created.
     * @param mockClass         The Class object representing the type of mock object to create.
     * @param values            A map containing custom property values to set on the mock object.
     * @param previousFieldName The name of the previous field, if any, to create a hierarchical property name.
     * @return                  A mock object of the specified class with either default values or custom values
     *                          set for its properties as defined in the 'values' map. Returns null if an error
     *                          occurs during the mock object creation.
     */
    private static <T> T createMock(Class<T> mockClass, Map<String, Object> values, String previousFieldName) {
        try {
            Constructor<T> constructor = mockClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            final var instance = constructor.newInstance();
            for (Method method : instance.getClass().getDeclaredMethods()) {
                if (isSetter(method)) {
                    if (isOnlyOneParameter(method)) {
                        final var parameter = method.getParameterTypes()[0];
                        method.invoke(instance, getDefaultValueOrParameter(parameter, method, values, previousFieldName));
                    }
                }
            }
            return instance;
        } catch (Exception ex) {
            logger.error("Error creating mock", ex);
            return null;
        }
    }

    private static boolean isOnlyOneParameter(Method method) {
        return method.getParameterTypes().length == 1;
    }

    private static boolean isSetter(Method method) {
        return method.getName().startsWith("set");
    }

    private static String getVarName(Method method, String previousFieldName) {
        var fieldName = method.getName().replaceAll("set", "");
        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
        return previousFieldName == null ? fieldName : String.format("%s.%s", previousFieldName, fieldName);
    }

    private static Object getDefaultValue(Class<?> classType) {
        final var type = classType.getName().toLowerCase();
        return TypeMapping.foundOne(type)
                .map(TypeMapping::getValue)
                .orElse(createMock(classType));
    }

    private static Object getDefaultValueOrParameter(Class<?> classType, Method method, Map<String, Object> values, final String previousFieldName) {
        final var varNameFixed = getVarName(method, previousFieldName);
        final var parameter = values.get(varNameFixed);
        if (parameter != null) {
            return parameter;
        }
        final var type = classType.getName().toLowerCase();
        return TypeMapping.foundOne(type)
                .map(TypeMapping::getValue)
                .orElse(createMock(classType, values, varNameFixed));
    }

}

enum TypeMapping {
    STRING("string", "string"),
    INT("int", 0),
    LONG("long", 0L),
    DOUBLE("double", 0.0),
    FLOAT("float", 0.0f),
    BOOLEAN("boolean", false),
    CHAR("char", ' '),
    BYTE("byte", (byte) 0),
    BIG_DECIMAL("bigdecimal", new BigDecimal("0.0")),
    TINY_INT("tinyint", (byte) 0),
    LOCAL_DATE("localdate", LocalDate.now());
    private final String name;
    private final Object value;

    TypeMapping(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public static Optional<TypeMapping> foundOne(final String name) {
        return Arrays.stream(TypeMapping.values())
                .filter(type -> name.contains(type.getName()))
                .findFirst();
    }
}