package com.asu.data.repository;

import java.util.HashSet;
import java.util.Set;

public class RepositoryUtils {

    public static Set<Class<?>> findRepositories(String basePackage) {

        // 🔥 Simplified (replace with ASM scanner later)
        Set<Class<?>> repos = new HashSet<>();

        // TODO: integrate with your ASMScanner
        // For now manually register or extend your scanner

        return repos;
    }
}