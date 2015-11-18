package org.jerkar.addin.springboot;

import java.io.File;

import org.jerkar.addin.springboot.JkSpringModules.Boot;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkDependencyExclusions;
import org.jerkar.api.depmanagement.JkVersionProvider;
import org.jerkar.api.java.JkClassLoader;
import org.jerkar.api.java.JkClasspath;
import org.jerkar.api.java.JkJavaProcess;
import org.jerkar.api.system.JkLog;
import org.jerkar.api.utils.JkUtilsObject;
import org.jerkar.tool.JkDoc;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

/**
 * A template class to extends for building Spring Boot application
 * 
 * @author Jerome Angibaud
 */
public class JkSpringbootBuild extends JkJavaBuild {
    
    private File execJar;
    
    protected JkSpringbootBuild() {
	this.tests.fork = true;
    }
    
    /**
     * Returns the version management used to resolve version dependencies. 
     * The default is the one suited for Spring Boot version 1.2.7.RELEASE.
     */
    protected JkSpringbootVersionManagement versionManagement() {
	return JkSpringbootVersionManagement.v1_2_7();
    }

    @Override
    public void pack() {
	super.pack();
	JkSpringbootPacker packer = JkSpringbootPacker.of(this.dependencyResolver(),
		this.versionManagement().orgSpringframeworkBootVersion).module(this.versionedModule());
	JkLog.start("Creating executable jar");
	this.execJar = packer.makeExecJar(this.packer().jarFile());
	JkLog.done();
    }

    // Formatter:off
    @Override
    protected JkDependencies dependencies() {
	return JkDependencies.builder()
		.on(Boot.STARTER)
		.on(Boot.STARTER_TEST, TEST).build();
    }

    /**
     * Returns the class name containing the main method to run the application.
     * If this method returns <code>null</code>, the the first compiled class having 
     * a main method is returned. </br>
     * Override this method to force the main class for running the application.
     */
    protected String mainClass() {
	return null;
    }
    
    @Override
    protected final JkVersionProvider versionProvider() {
        return this.versionManagement().versionProvider();
    }
    
    @Override
    protected final JkDependencyExclusions dependencyExclusions() {
        return this.versionManagement().dependencyExclusions();
    }
    
    @JkDoc("Run the application based on the compiled classes (not on produced jar). It supposes the class to be yet compiled.")
    public void run() {
	JkClasspath classpath = JkClasspath.of(this.classDir()).and(dependencyResolver().get(RUNTIME));
	String mainClass =JkUtilsObject.firstNonNull(this.mainClass(), JkClassLoader.findMainClass(this.classDir()));
	JkJavaProcess.of().andClasspath(classpath).runClassSync(mainClass);
    }
    
    @JkDoc("Run the application based on the produced executable jar. It supposes jar to be yet produced.")
    public void runJar() {
	String debug = JkLog.verbose() ? "--debug" : "";
	JkJavaProcess.of().runJarSync(execJar, debug);
    }

}
