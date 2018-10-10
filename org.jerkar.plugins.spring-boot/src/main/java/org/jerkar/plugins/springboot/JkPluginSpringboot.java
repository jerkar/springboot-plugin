package org.jerkar.plugins.springboot;

import org.jerkar.api.depmanagement.*;
import org.jerkar.api.file.JkPathSequence;
import org.jerkar.api.java.JkClassLoader;
import org.jerkar.api.java.project.JkJavaProject;
import org.jerkar.api.java.project.JkJavaProjectMaker;
import org.jerkar.api.system.JkException;
import org.jerkar.api.system.JkLog;
import org.jerkar.api.tooling.JkPom;
import org.jerkar.tool.JkDoc;
import org.jerkar.tool.JkDocPluginDeps;
import org.jerkar.tool.JkPlugin;
import org.jerkar.tool.JkRun;
import org.jerkar.tool.builtins.java.JkPluginJava;

import java.nio.file.Files;
import java.nio.file.Path;

@JkDoc("Provides enhancement to Java plugin in order to produce a startable Springboot jar or your application.\n" +
        "The main produced artifact is the springboot one (embedding all dependencies) while the artifact classified as 'original' stands for the vanilla jar.\n" +
        "Dependency versions are resolved against BOM provided by Spring Boot team according Spring Boot version you use.")
@JkDocPluginDeps(JkPluginJava.class)
public final class JkPluginSpringboot extends JkPlugin {

    @JkDoc("Version of Spring Boot version used to resolve dependency versions.")
    public String springbootVersion = "2.0.3.RELEASE";

    @JkDoc("Class name holding main method to start Spring Boot. If null, Jerkar will try to guess it at build time.")
    public String mainClassName;

    private final JkPluginJava java;

    /**
     * Right after to be instantiated, plugin instances are likely to configured by the owning build.
     * Therefore, every plugin members that are likely to be configured by the owning build must be
     * initialized in the constructor.
     */
    protected JkPluginSpringboot(JkRun run) {
        super(run);
        java = run.plugins().get(JkPluginJava.class);
    }

    @Override
    @JkDoc("Modifies the Java project from Java plugin in such this project produces a SpringBoot jar as the main artifact.")
    protected void activate() {
        activate(java.project());
    }

    public void activate(JkJavaProject project) {
        JkJavaProjectMaker maker = project.maker();

        // resolve dependency versions upon springboot provided ones
        JkRepoSet repos = maker.getDependencyResolver().repositories();
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
                    springbootVersion, mainClassName);
        });
    }

    public JkPluginJava java() {
        return java;
    }

    public static JkVersionProvider resolveVersions(JkRepoSet repos, String springbootVersion) {
        JkModuleDependency moduleDependency = JkModuleDependency.of(
                "org.springframework.boot", "spring-boot-dependencies", springbootVersion).ext("pom");
        JkLog.info("Fetch Springboot dependency versions from " + moduleDependency);
        Path pomFile = repos.get(moduleDependency);
        if (pomFile == null || !Files.exists(pomFile)) {
            throw new JkException(moduleDependency + " not found");
        }
        JkPom pom = JkPom.of(pomFile);
        JkLog.info("Springboot dependency version will be resolved from " + pomFile);
        return pom.versionProvider();
    }

    public static void createBootJar(Path original, JkPathSequence libsToInclude, Path bootLoaderJar, Path targetJar,
                                     String springbootVersion, String mainClassName) {
        String className = mainClassName != null ? mainClassName : JkClassLoader.findMainClass(original);
        SpringbootPacker.of(libsToInclude, bootLoaderJar, className, springbootVersion).makeExecJar(original, targetJar);
    }

}
