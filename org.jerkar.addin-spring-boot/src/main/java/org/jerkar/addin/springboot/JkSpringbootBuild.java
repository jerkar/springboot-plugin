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
import org.jerkar.api.tooling.JkCodeWriterForBuildClass;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsIO;
import org.jerkar.api.utils.JkUtilsObject;
import org.jerkar.api.utils.JkUtilsString;
import org.jerkar.tool.JkDoc;
import org.jerkar.tool.JkScaffolder;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;
import org.jerkar.tool.builtins.javabuild.JkJavaPacker;
import org.jerkar.tool.builtins.javabuild.JkJavaPacker.JkExtraPacking;

/**
 * A template class to extends for building Spring Boot application
 * 
 * @author Jerome Angibaud
 */
public class JkSpringbootBuild extends JkJavaBuild {

    private static final String JK_IMPORT = "org.jerkar:addin-spring-boot:1.4.2.+";

    protected JkSpringbootBuild() {
        this.tests.fork = true;
    }

    /**
     * Returns the version management used to resolve version dependencies. The
     * default is the one suited for Spring Boot version 1.3.1.RELEASE.
     */
    protected JkSpringbootVersionManagement versionManagement() {
        return JkSpringbootVersionManagement.v1_4_2();
    }

    
    private void makeExecutableJar() {
        JkSpringbootPacker packer = JkSpringbootPacker
                .of(this.dependencyResolver(), this.versionManagement().springbootVersion())
                .module(this.versionedModule());
        JkLog.startln("Creating Springboot executable jar");
        JkUtilsFile.move(this.packer().jarFile(), originalJar() );
        packer.makeExecJar(originalJar(), this.packer().jarFile());
        JkLog.done();
    }
    
    public File originalJar() {
        return new File(this.packer().jarFile() + ".original");
    }
    
    @Override
    protected JkJavaPacker createPacker() {
        return super.createPacker().builder().extraAction(new JkExtraPacking() {
            
            @Override
            public void process(JkJavaBuild build) {
                makeExecutableJar();
                
            }
            
            
            
        }).build();
    }

    // Formatter:off
    @Override
    protected JkDependencies dependencies() {
        return JkDependencies.builder().on(Boot.STARTER).on(Boot.STARTER_TEST, TEST).build();
    }

    /**
     * Returns the class name containing the main method to run the application.
     * If this method returns <code>null</code>, the the first compiled class
     * having a main method is returned. </br>
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

    /**
     * Override this method if you need to need to run the application with
     * special setting on the Jvm (agent, memory ,...)
     */
    protected JkJavaProcess javaProcess() {
        return JkJavaProcess.of();
    }

    @JkDoc("Run the spring-boot application based on the compiled classes (not on produced jar). It supposes the class to be yet compiled.")
    public void run() {
        JkClasspath classpath = JkClasspath.of(this.classDir()).and(dependencyResolver().get(RUNTIME));
        String mainClass = JkUtilsObject.firstNonNull(this.mainClass(), JkClassLoader.findMainClass(this.classDir()));
        javaProcess().andClasspath(classpath).runClassSync(mainClass);
    }

    @JkDoc("Run the spring-boot application based on the produced executable jar. It supposes jar to be yet produced.")
    public void runJar() {
        String debug = JkLog.verbose() ? "--debug" : "";
        javaProcess().runJarSync(this.packer().jarFile(), debug);
    }

    @Override
    protected JkScaffolder scaffolder() {
        JkScaffolder scaffolder = super.scaffolder();
        Object codeBuilder = scaffolder.buildClassCodeWriter();
        if (codeBuilder instanceof JkCodeWriterForBuildClass) {
            JkCodeWriterForBuildClass coder = (JkCodeWriterForBuildClass) codeBuilder;
            coder.extendedClass = "JkSpringbootBuild";
            coder.imports.remove("org.jerkar.tool.builtins.javabuild.JkJavaBuild");
            coder.imports.add("org.jerkar.addin.springboot.JkSpringbootBuild");
            coder.imports.add("org.jerkar.tool.JkImport");
            coder.staticImports.add("org.jerkar.addin.springboot.JkSpringModules.*");
            coder.jkImports.add(JK_IMPORT);
            coder.dependencies = null;
            coder.version = "0.1-SNAPSHOT";
            StringBuilder methodDep = new StringBuilder();
            methodDep.append("    @Override\n").append("    protected JkDependencies dependencies() {\n")
                    .append("        return JkDependencies.builder()\n").append("            .on(Boot.STARTER_WEB)\n")
                    .append("            .on(Boot.STARTER_TEST, TEST).build();\n").append("    }");
            coder.extraMethods.add(methodDep.toString());
        }
        scaffolder.extraAction(new Runnable() {
            
            @Override
            public void run() {
                writeScaffoldedClasses();
            }
        });
        return scaffolder;
    }
    
    private void writeScaffoldedClasses() {
        String packageName = JkUtilsString.conformPackageName(this.moduleId().fullName());
        String applicationSourceTemplate = JkUtilsIO.read(JkSpringbootBuild.class.getResource("templates/Application.java.template"));
        String helloControllerSourceTemplate = JkUtilsIO.read(JkSpringbootBuild.class.getResource("templates/HelloController.java.template"));
        String helloControllerTestSourceTemplate = JkUtilsIO.read(JkSpringbootBuild.class.getResource("templates/HelloControllerTest.java.template"));
        String applicationSource = applicationSourceTemplate.replace("{packageName}", packageName);
        String helloControllerSource = helloControllerSourceTemplate.replace("{packageName}", packageName);
        String helloControllerTestSource = helloControllerTestSourceTemplate.replace("{packageName}", packageName);
        String path = packageName.replace('.', '/') + "/";
        File sourceFolder = this.editedSources().roots().get(0);
        File application = new File (sourceFolder, path + "Application.java");
        JkUtilsFile.writeStringAtTop(application, applicationSource);
        File helloCopntroller = new File (sourceFolder, path + "HelloController.java");
        JkUtilsFile.writeStringAtTop(helloCopntroller, helloControllerSource);
        File testSourceFolder = this.unitTestEditedSources().roots().get(0);
        File helloTestConntroller = new File (testSourceFolder, path + "HelloControllerTest.java");
        JkUtilsFile.writeStringAtTop(helloTestConntroller, helloControllerTestSource);   
    }
}
