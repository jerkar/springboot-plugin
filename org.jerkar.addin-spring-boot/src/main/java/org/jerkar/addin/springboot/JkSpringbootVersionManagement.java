package org.jerkar.addin.springboot;

import org.jerkar.api.depmanagement.JkDependencyExclusions;
import org.jerkar.api.depmanagement.JkVersionProvider;

/**
 * Version management suggested by Spring Boot. 
 * 
 * @author Jerome Angibaud
 * @formatter:off
 */
public abstract class JkSpringbootVersionManagement {
    
    /** Creates a version management suited for Spring Boot version 1.3.1 */
    public static JkSpringbootVersionManagement v1_3_1() {
        return new Version1_3_1();
    }
    
    /** Creates a  version management suited for Spring Boot version 1.3.0 */
    public static JkSpringbootVersionManagement v1_3_0() {
        return new Version1_3_0();
    }
    
    /** Creates a  version management suited for Spring Boot version 1.2.8 */
    public static JkSpringbootVersionManagement v1_2_8() {
        return new Version1_2_8();
    }
    
    /** Creates a version management suited for Spring Boot version 1.2.7 */
    public static JkSpringbootVersionManagement v1_2_7() {
        return new Version1_2_7();
    }
    
    /** Returns versions provider */
    public final JkVersionProvider versionProvider;
    
    /** Returns dependency exclusions */
    public final JkDependencyExclusions exclusions;
    
    protected JkSpringbootVersionManagement() {
        this.versionProvider = versionProvider();
        this.exclusions = dependencyExclusions();
    }
    
    public final String springbootVersion() {
        return this.versionProvider.versionOf(JkSpringModules.Boot.STARTER).name();
    }

    protected abstract JkVersionProvider versionProvider();
    
    protected abstract JkDependencyExclusions dependencyExclusions();
    
}
