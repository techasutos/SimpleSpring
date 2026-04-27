package com.asu.annotations;

import org.objectweb.asm.*;

public class AnnotationMetadataVisitor extends ClassVisitor {

    private AnnotationMetadata metadata;

    public AnnotationMetadataVisitor() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        metadata = new AnnotationMetadata(name.replace("/", "."));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        metadata.addAnnotation(descriptor);
        return super.visitAnnotation(descriptor, visible);
    }

    public AnnotationMetadata getMetadata() {
        return metadata;
    }
}
