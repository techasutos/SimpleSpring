package com.asu.annotations;

import org.objectweb.asm.ClassReader;
import java.io.InputStream;

public class MetadataReader {

    private AnnotationMetadata metadata;

    public MetadataReader(InputStream is) throws Exception {
        ClassReader reader = new ClassReader(is);
        AnnotationMetadataVisitor visitor = new AnnotationMetadataVisitor();
        reader.accept(visitor, ClassReader.SKIP_DEBUG);
        this.metadata = visitor.getMetadata();
    }

    public AnnotationMetadata getAnnotationMetadata() {
        return metadata;
    }
}
