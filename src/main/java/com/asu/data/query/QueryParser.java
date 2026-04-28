package com.asu.data.query;

import java.lang.reflect.Method;

public class QueryParser {

    public static QueryModel parse(Method method) {

        String name = method.getName();

        if (!name.startsWith("findBy")) {
            throw new RuntimeException("Only findBy supported");
        }

        String body = name.substring("findBy".length());

        // split AND / OR
        String[] rawFields = body.split("And|Or");

        String[] operators = new String[rawFields.length];
        String[] logicals = new String[rawFields.length];

        for (int i = 0; i < rawFields.length; i++) {

            String f = rawFields[i];

            if (f.endsWith("GreaterThan")) {
                operators[i] = "GT";
                rawFields[i] = f.replace("GreaterThan", "");
            } else if (f.endsWith("LessThan")) {
                operators[i] = "LT";
                rawFields[i] = f.replace("LessThan", "");
            } else if (f.endsWith("Like")) {
                operators[i] = "LIKE";
                rawFields[i] = f.replace("Like", "");
            } else {
                operators[i] = "EQ";
            }

            logicals[i] = "AND"; // default
        }

        return new QueryModel(
                normalize(rawFields),
                operators,
                logicals
        );
    }

    private static String[] normalize(String[] fields) {

        for (int i = 0; i < fields.length; i++) {
            fields[i] = Character.toLowerCase(fields[i].charAt(0))
                    + fields[i].substring(1);
        }

        return fields;
    }
}