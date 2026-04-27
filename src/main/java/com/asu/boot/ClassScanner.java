package com.asu.boot;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ClassScanner {

    public List<Class<?>> scan(String basePackage) {

        List<Class<?>> classes = new ArrayList<>();

        String path = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(path);

            while (resources.hasMoreElements()) {

                File dir = new File(resources.nextElement().getFile());

                for (File file : Objects.requireNonNull(dir.listFiles())) {

                    if (file.getName().endsWith(".class")) {

                        String className = basePackage + "."
                                + file.getName().replace(".class", "");

                        classes.add(Class.forName(className));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }
}
