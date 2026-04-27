package com.asu.annotations;

import com.asu.condition.Condition;

public class OnClassCondition implements Condition {

    @Override
    public boolean matches(AnnotationMetadata metadata, ClassLoader cl) {

        // read annotation attributes manually (simplified)
        // assume we parsed values elsewhere

        String requiredClass = "com.mysql.cj.jdbc.Driver";

        try {
            cl.loadClass(requiredClass);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
