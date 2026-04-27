package com.asu.scanning;

import java.io.InputStream;
import java.net.URL;
import java.io.File;
import java.util.*;

import com.asu.annotations.ClassMetadata;
import com.asu.annotations.ComponentClassVisitor;
import org.objectweb.asm.ClassReader;

public class ASMScanner {

    public List<ClassMetadata> scan(String basePackage) {

        List<ClassMetadata> classes = new ArrayList<>();

        String path = basePackage.replace(".", "/");

        URL resource = getClass().getClassLoader().getResource(path);
        File dir = new File(resource.getFile());

        for (File file : Objects.requireNonNull(dir.listFiles())) {

            if (file.getName().endsWith(".class")) {

                try (InputStream is = getClass().getClassLoader()
                        .getResourceAsStream(path + "/" + file.getName())) {

                    ClassReader reader = new ClassReader(is);

                    ComponentClassVisitor visitor = new ComponentClassVisitor();

                    reader.accept(visitor, ClassReader.SKIP_DEBUG);

                    classes.add(visitor.getMetadata());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return classes;
    }
}
