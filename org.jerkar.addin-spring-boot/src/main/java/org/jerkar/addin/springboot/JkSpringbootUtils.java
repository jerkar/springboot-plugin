package org.jerkar.addin.springboot;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import org.jerkar.api.file.JkFileTree;
import org.jerkar.api.java.JkClassLoader;
import org.jerkar.api.utils.JkUtilsReflect;

public final class JkSpringbootUtils {
    
    private JkSpringbootUtils() {
    }
    
    public static String findMainClass(File classDir) {
	JkClassLoader classLoader = JkClassLoader.current().child(classDir);
	JkFileTree classesFileTree = JkFileTree.of(classDir).include("**/*.class");
	for(Iterator<Class<?>> it = classLoader.iterateClassesIn(classesFileTree.asSet()); it.hasNext();) {
	    Class<?> clazz = it.next();
	    Method mainMethod = JkUtilsReflect.getMethodOrNull(clazz, "main", String[].class);
	    int modifiers = mainMethod.getModifiers();
	    if (mainMethod != null && Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
		return clazz.getName();
	    }
	}
	throw new IllegalStateException("Can't find any class with a main method.");
    }
   
    

}
