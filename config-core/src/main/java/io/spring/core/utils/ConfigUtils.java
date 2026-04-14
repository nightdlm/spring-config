package io.spring.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class ConfigUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    //基础类型转换
    public static void convert(Field field, String value ) {
        final Class<?> targetType = field.getType();
        try {
            if (targetType == String.class) {
                field.set(null,value);
            } else if (targetType == Integer.class || targetType == int.class) {
                field.setInt(null,Integer.parseInt(value));
            } else if (targetType == Long.class || targetType == long.class) {
                field.setLong(null,Long.parseLong(value));
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                if (targetType == Boolean.class) {
                    field.set(null, Boolean.valueOf(value));
                } else {
                    field.setBoolean(null, Boolean.parseBoolean(value));
                }
            } else if (targetType == Double.class || targetType == double.class) {
                field.setDouble(null,Double.parseDouble(value));
            } else if (targetType == Float.class || targetType == float.class) {
                field.setFloat(null,Float.parseFloat(value));
            } else if (targetType == Character.class || targetType == char.class) {
                field.setChar(null,value.charAt(0));
            } else if (targetType == Byte.class || targetType == byte.class) {
                field.setByte(null,Byte.parseByte(value));
            } else if (targetType == List.class) {
                field.set(null, objectMapper.readValue(value, new TypeReference<List<Object>>() {}));
            } else if (targetType == Map.class) {
                field.set(null, objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {}));
            }
            else {
                throw new IllegalArgumentException("Unsupported target type: " + targetType);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for type " + targetType + ": " + field.getName() + ":" + value, e);
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON value for type " + targetType + ": " + field.getName() + ":" + value, e);
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid value for type " + targetType + ": " + field.getName() + ":" + value, e);
        }
    }
}
