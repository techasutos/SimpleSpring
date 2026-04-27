package com.asu.annotations;

import org.objectweb.asm.*;

public class ComponentClassVisitor extends ClassVisitor {

    private String className;
    private boolean isComponent = false;

    public ComponentClassVisitor() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.className = name.replace("/", ".");
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

        // Descriptor format: Lcom/example/Component;
        if (descriptor.contains("Component")) {
            isComponent = true;
        }

        return super.visitAnnotation(descriptor, visible);
    }

    public ClassMetadata getMetadata() {
        return new ClassMetadata(className, isComponent);
    }
}
