package com.asu.scanning;

import com.asu.annotations.ClassMetadata;
import org.objectweb.asm.*;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ASMScanner {

    public List<ClassMetadata> scan(String basePackage) {

        List<ClassMetadata> result = new ArrayList<>();

        try {
            String path = basePackage.replace(".", "/");

            Enumeration<URL> resources =
                    Thread.currentThread()
                            .getContextClassLoader()
                            .getResources(path);

            while (resources.hasMoreElements()) {

                URL url = resources.nextElement();

                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    scanFileSystem(url, basePackage, result);
                }

                else if ("jar".equals(protocol)) {
                    scanJar(url, path, result);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    // -----------------------------
    // FILE SYSTEM SCAN
    // -----------------------------
    private void scanFileSystem(URL url,
                                String basePackage,
                                List<ClassMetadata> result) throws Exception {

        String filePath = url.getFile();

        scanDirectory(
                new java.io.File(filePath),
                basePackage,
                result
        );
    }

    private void scanDirectory(java.io.File dir,
                               String packageName,
                               List<ClassMetadata> result) throws Exception {

        if (!dir.exists()) return;

        for (java.io.File file : Objects.requireNonNull(dir.listFiles())) {

            if (file.isDirectory()) {
                scanDirectory(
                        file,
                        packageName + "." + file.getName(),
                        result
                );
            }

            else if (file.getName().endsWith(".class")) {

                String className = packageName + "."
                        + file.getName().replace(".class", "");

                ClassMetadata meta = readMetadata(
                        className,
                        file.toURI().toURL().openStream()
                );

                result.add(meta);
            }
        }
    }

    // -----------------------------
    // JAR SCAN
    // -----------------------------
    private void scanJar(URL url,
                         String basePath,
                         List<ClassMetadata> result) throws Exception {

        URLConnection connection = url.openConnection();

        if (!(connection instanceof java.net.JarURLConnection)) return;

        JarFile jarFile =
                ((java.net.JarURLConnection) connection).getJarFile();

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {

            JarEntry entry = entries.nextElement();

            String name = entry.getName();

            if (name.startsWith(basePath) && name.endsWith(".class")) {

                String className = name
                        .replace("/", ".")
                        .replace(".class", "");

                InputStream is = jarFile.getInputStream(entry);

                ClassMetadata meta = readMetadata(className, is);

                result.add(meta);
            }
        }
    }

    // -----------------------------
    // ASM METADATA READER
    // -----------------------------
    private ClassMetadata readMetadata(String className,
                                       InputStream inputStream) throws Exception {

        ClassMetadata metadata = new ClassMetadata();
        metadata.setClassName(className);

        ClassReader reader = new ClassReader(inputStream);

        reader.accept(new ClassVisitor(Opcodes.ASM9) {

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

                String annotation = Type.getType(desc).getClassName();

                // 🔥 Detect components
                if (annotation.equals("com.asu.annotations.Component") ||
                        annotation.equals("com.asu.annotations.Service") ||
                        annotation.equals("com.asu.annotations.Repository")) {

                    metadata.setComponent(true);
                }

                if (annotation.equals("com.asu.annotations.Configuration")) {
                    metadata.setConfiguration(true);
                }

                return super.visitAnnotation(desc, visible);
            }

        }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);

        return metadata;
    }
}