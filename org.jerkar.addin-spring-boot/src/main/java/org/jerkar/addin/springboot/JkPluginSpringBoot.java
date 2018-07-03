package org.jerkar.addin.springboot;

import org.jerkar.api.depmanagement.*;
import org.jerkar.api.file.JkPathSequence;
import org.jerkar.api.file.JkPathTree;
import org.jerkar.api.java.JkClassLoader;
import org.jerkar.api.java.JkClasspath;
import org.jerkar.api.project.java.JkJavaProject;
import org.jerkar.api.project.java.JkJavaProjectMaker;
import org.jerkar.api.system.JkException;
import org.jerkar.api.tooling.JkPom;
import org.jerkar.api.utils.JkUtilsReflect;
import org.jerkar.api.utils.JkUtilsString;
import org.jerkar.tool.JkBuild;
import org.jerkar.tool.JkPlugin;
import org.jerkar.tool.builtins.java.JkPluginJava;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * A template class to extends for building Spring Boot application
 * 
 * @author Jerome Angibaud
 */
public class JkPluginSpringBoot extends JkPlugin {

    public String springbootVersion = "2.0.3.RELEASE";

    private final JkPluginJava java;


    /**
     * Right after to be instantiated, plugin instances are likely to configured by the owning build.
     * Therefore, every plugin members that are likely to be configured by the owning build must be
     * initialized in the constructor.
     *
     * @param build
     */
    protected JkPluginSpringBoot(JkBuild build) {
        super(build);
        java = build.plugins().get(JkPluginJava.class);
    }

    @Override
    protected void decorateBuild() {

        JkJavaProject project = java.project();
        JkJavaProjectMaker maker = project.maker();

        // resolve dependency versions upon springboot provided ones
        JkRepos repos = maker.getDependencyResolver().repositories();
        JkVersionProvider versionProvider = resolveVersions(repos, springbootVersion);
        project.setDependencies(project.getDependencies().andVersionProvider(versionProvider));

        // add bootable jar artifact
        JkArtifactId boot = JkArtifactId.of("boot", "jar");
        Path target = java.project().maker().artifactPath(boot);
        JkVersion loaderVersion = versionProvider.versionOf(JkSpringModules.Boot.LOADER);
        Path bootloader = maker.getDependencyResolver().repositories()
                .get(JkSpringModules.Boot.LOADER, loaderVersion.name());
        JkPathSequence nestedLibs = maker.runtimeDependencies(maker.mainArtifactId());
        project.maker().defineArtifact(boot, () -> {
            createBootJar(maker.mainArtifactPath(), nestedLibs, bootloader, target);
        });
        java.addArtifactToProduce(boot);

    }

    public static JkVersionProvider resolveVersions(JkRepos repo, String springbootVersion) {
        JkModuleDependency moduleDependency = JkModuleDependency.of(
                "org.springframework.boot", "spring-boot-dependencies", springbootVersion).ext("pom");
        Path pomFile = repo.get(moduleDependency);
        if (pomFile == null || !Files.exists(pomFile)) {
            throw new JkException(moduleDependency + " not found");
        }
        JkPom pom = JkPom.of(pomFile);
        return pom.versionProvider();
    }

    public static void createBootJar(Path original, JkPathSequence libsToInclude, Path bootLoaderJar, Path targetJar) {
        String className = JkClassLoader.findMainClass(original);
        SpringbootPacker.of(libsToInclude, bootLoaderJar, className).makeExecJar(original, targetJar);
    }





}
