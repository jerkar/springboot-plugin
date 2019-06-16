package dev.jeka.plugins.springboot;

import dev.jeka.core.api.depmanagement.*;
import dev.jeka.core.api.file.JkPathSequence;
import dev.jeka.core.api.java.JkUrlClassLoader;
import dev.jeka.core.api.java.project.JkJavaProject;
import dev.jeka.core.api.java.project.JkJavaProjectMaker;
import dev.jeka.core.api.system.JkException;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.tooling.JkPom;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkDocPluginDeps;
import dev.jeka.core.tool.JkPlugin;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import java.nio.file.Files;
import java.nio.file.Path;

@JkDoc("Provides enhancement to Java plugin in order to produce a startable Springboot jar for your application.\n" +
        "The main produced artifact is the springboot one (embedding all dependencies) while the artifact classified as 'original' stands for the vanilla jar.\n" +
        "Dependency versions are resolved against BOM provided by Spring Boot team according Spring Boot version you use.")
@JkDocPluginDeps(JkPluginJava.class)
public final class JkPluginSpringboot extends JkPlugin {

    @JkDoc("Version of Spring Boot version used to resolve dependency versions.")
    public String springbootVersion = "2.0.3.RELEASE";

    @JkDoc("Class name holding main method to start Spring Boot. If null, Jerkar will try to guess it at build time.")
    public String mainClassName;

    @JkDoc("If true, Spring Milestone or Snapshot Repository will be used to fetch non release version of spring modules")
    public boolean autoSpringRepo = true;

    private final JkPluginJava java;

    /**
     * Right after to be instantiated, plugin instances are likely to configured by the owning build.
     * Therefore, every plugin members that are likely to be configured by the owning build must be
     * initialized in the constructor.
     */
    protected JkPluginSpringboot(JkCommands jkCommands) {
        super(jkCommands);
        java = jkCommands.getPlugins().get(JkPluginJava.class);
    }

    @Override
    @JkDoc("Modifies the Java project from Java plugin in such this project produces a SpringBoot jar as the main artifact.")
    protected void activate() {
        activate(java.getProject());
    }

    public void activate(JkJavaProject project) {
        JkJavaProjectMaker maker = project.getMaker();

        // Add spring snapshot or milestone repos if necessary
        JkVersion version = JkVersion.of(springbootVersion);
        if (autoSpringRepo && version.hasBlockAt(3)) {
            JkRepoSet repos = JkSpringRepos.getRepoForVersion(version.getBlock(3));
            maker.setDependencyResolver(maker.getDependencyResolver().andRepos(repos));
        }

        // resolve dependency versions upon springboot provided ones
        JkRepoSet repos = maker.getDependencyResolver().getRepos();
        JkVersionProvider versionProvider = resolveVersions(repos, springbootVersion);
        project.setDependencies(project.getDependencies().andVersionProvider(versionProvider));

        maker.removeArtifact(maker.getMainArtifactId());

        // add original jar artifact
        JkArtifactId original = JkArtifactId.of("original", "jar");
        Path originalPath = maker.getArtifactPath(original);
        JkArtifactId mainArtifactId = maker.getMainArtifactId();
        maker.putArtifact(original, () -> maker.getTasksForPackaging().createBinJar(maker.getArtifactPath(original)));

        // define bootable jar as main artifact
        JkVersion loaderVersion = versionProvider.getVersionOf(JkSpringModules.Boot.LOADER);
        Path bootloader = maker.getDependencyResolver().getRepos()
                .get(JkSpringModules.Boot.LOADER, loaderVersion.getValue());
        maker.putArtifact(mainArtifactId, () -> {
            if (!Files.exists(originalPath)) {
                maker.makeArtifact(original);
            }
            final JkPathSequence nestedLibs = maker.fetchRuntimeDependencies(mainArtifactId);
            createBootJar(originalPath, nestedLibs, bootloader, maker.getMainArtifactPath(),
                    springbootVersion, mainClassName);
        });
    }

    public JkPluginJava javaPlugin() {
        return java;
    }

    public static JkVersionProvider resolveVersions(JkRepoSet repos, String springbootVersion) {
        JkModuleDependency moduleDependency = JkModuleDependency.of(
                "org.springframework.boot", "spring-boot-dependencies", springbootVersion).withExt("pom");
        JkLog.info("Fetch Springboot dependency versions from " + moduleDependency);
        Path pomFile = repos.get(moduleDependency);
        if (pomFile == null || !Files.exists(pomFile)) {
            throw new JkException(moduleDependency + " not found");
        }
        JkPom pom = JkPom.of(pomFile);
        JkLog.info("Springboot dependency version will be resolved from " + pomFile);
        return pom.getVersionProvider();
    }

    public static void createBootJar(Path original, JkPathSequence libsToInclude, Path bootLoaderJar, Path targetJar,
                                     String springbootVersion, String mainClassName) {
        String className = mainClassName != null ? mainClassName : JkUrlClassLoader.findMainClass(original);
        SpringbootPacker.of(libsToInclude, bootLoaderJar, className, springbootVersion).makeExecJar(original, targetJar);
    }

}
