package com.asu.web.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class JsonMapper {

    // Serialize object → JSON
    public String toJson(Object obj) {

        if (obj == null) return "null";

        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();

            StringBuilder json = new StringBuilder("{");

            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);

                String name = fields[i].getName();
                Object value = fields[i].get(obj);

                json.append("\"").append(name).append("\":");

                if (value instanceof String) {
                    json.append("\"").append(value).append("\"");
                } else {
                    json.append(value);
                }

                if (i < fields.length - 1) json.append(",");
            }

            json.append("}");
            return json.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Deserialize JSON → Object (basic)
    public <T> T fromJson(String json, Class<T> clazz) {

        try {
            Map<String, String> map = parseJson(json);

            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                String value = map.get(field.getName());

                if (value != null) {
                    field.set(instance, convert(value, field.getType()));
                }
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> parseJson(String json) {

        Map<String, String> map = new HashMap<>();

        json = json.replaceAll("[{}\"]", "");
        String[] pairs = json.split(",");

        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }

        return map;
    }

    private Object convert(String value, Class<?> type) {

        if (type == String.class) return value;
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);

        return value;
    }
}
