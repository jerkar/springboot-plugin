package org.jerkar.addin.springboot;

import java.io.File;

import org.jerkar.addin.springboot.JkSpringModules.Boot;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkDependencyExclusions;
import org.jerkar.api.depmanagement.JkVersionProvider;
import org.jerkar.api.system.JkLog;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

public class JkSpringbootBuild extends JkJavaBuild {
    
    private JkSpringbootVersionManagement versionManagement = JkSpringbootVersionManagement.v1_2_7();

    private File execJar;
    
    protected JkSpringbootVersionManagement versionManagement() {
	return versionManagement;
    }

    @Override
    public void pack() {
	super.pack();
	JkSpringbootPacker packer = JkSpringbootPacker.of(this.dependencyResolver(),
		this.versionManagement.orgSpringframeworkBootVersion).module(this.versionedModule());
	JkLog.start("Creating executable jar");
	this.execJar = packer.makeExecJar(this.packer().jarFile());
	JkLog.done();
    }

    @Override
    protected JkDependencies dependencies() {
	return JkDependencies.builder().on(Boot.STARTER).on(Boot.STARTER_TEST).scope(TEST).build();
    }

    @Override
    protected final JkVersionProvider versionProvider() {
	return this.versionManagement().versionProvider();
    }

    @Override
    protected JkDependencyExclusions dependencyExclusions() {
	return this.versionManagement().dependencyExclusions();
    }
   
    

}
