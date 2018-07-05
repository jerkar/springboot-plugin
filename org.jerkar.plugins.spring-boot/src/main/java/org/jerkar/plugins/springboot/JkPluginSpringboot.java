package org.jerkar.plugins.springboot;

import org.jerkar.api.depmanagement.*;
import org.jerkar.api.file.JkPathSequence;
import org.jerkar.api.java.JkClassLoader;
import org.jerkar.api.project.java.JkJavaProject;
import org.jerkar.api.project.java.JkJavaProjectMaker;
import org.jerkar.api.system.JkException;
import org.jerkar.api.tooling.JkPom;
import org.jerkar.api.utils.JkUtilsPath;
import org.jerkar.tool.JkBuild;
import org.jerkar.tool.JkPlugin;
import org.jerkar.tool.builtins.java.JkPluginJava;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A template class to extends for building Spring Boot application
 * 
 * @author Jerome Angibaud
 */
public final class JkPluginSpringboot extends JkPlugin {

    private String springbootVersion = "2.0.3.RELEASE";

    private final JkPluginJava java;

    /**
     * Right after to be instantiated, plugin instances are likely to configured by the owning build.
     * Therefore, every plugin members that are likely to be configured by the owning build must be
     * initialized in the constructor.
     *
     * @param build
     */
    protected JkPluginSpringboot(JkBuild build) {
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

        // add original jar artifact
        JkArtifactId original = JkArtifactId.of("original", "jar");
        Path originalPath = maker.artifactPath(original);
        maker.defineArtifact(original, () -> maker.makeBinJar(originalPath));

        // define bootable jar as main artifact
        JkVersion loaderVersion = versionProvider.versionOf(JkSpringModules.Boot.LOADER);
        Path bootloader = maker.getDependencyResolver().repositories()
                .get(JkSpringModules.Boot.LOADER, loaderVersion.value());
        project.maker().defineArtifact(maker.mainArtifactId(), () -> {
            if (!Files.exists(originalPath)) {
                maker.makeArtifact(original);
            }
            final JkPathSequence nestedLibs = maker.runtimeDependencies(maker.mainArtifactId());
            createBootJar(originalPath, nestedLibs, bootloader, maker.mainArtifactPath(),
                    springbootVersion);
        });
    }

    public JkPluginJava java() {
        return java;
    }

    public String getSpringbootVersion() {
        return springbootVersion;
    }

    public void setSpringbootVersion(String springbootVersion) {
        this.springbootVersion = springbootVersion;
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

    public static void createBootJar(Path original, JkPathSequence libsToInclude, Path bootLoaderJar, Path targetJar,
                                     String springbootVersion) {
        String className = JkClassLoader.findMainClass(original);
        SpringbootPacker.of(libsToInclude, bootLoaderJar, className, springbootVersion).makeExecJar(original, targetJar);
    }

}
