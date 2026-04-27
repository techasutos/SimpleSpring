package com.asu.resource;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.*;
import java.net.*;
import java.io.File;

public class ClassPathScanner {

    public List<String> scan(String basePackage) throws Exception {

        List<String> classNames = new ArrayList<>();
        String path = basePackage.replace(".", "/");

        Enumeration<URL> resources =
                Thread.currentThread().getContextClassLoader().getResources(path);

        while (resources.hasMoreElements()) {

            URL url = resources.nextElement();

            if (url.getProtocol().equals("file")) {
                scanDirectory(new File(url.toURI()), basePackage, classNames);
            }

            if (url.getProtocol().equals("jar")) {
                scanJar(url, path, classNames);
            }
        }

        return classNames;
    }

    private void scanDirectory(File dir, String pkg, List<String> classes) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(file, pkg + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                classes.add(pkg + "." + file.getName().replace(".class", ""));
            }
        }
    }

    private void scanJar(URL url, String path, List<String> classes) throws Exception {

        JarURLConnection conn = (JarURLConnection) url.openConnection();
        JarFile jar = conn.getJarFile();

        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {

            JarEntry entry = entries.nextElement();

            String name = entry.getName();

            if (name.startsWith(path) && name.endsWith(".class")) {
                classes.add(name.replace("/", ".").replace(".class", ""));
            }
        }
    }
}
