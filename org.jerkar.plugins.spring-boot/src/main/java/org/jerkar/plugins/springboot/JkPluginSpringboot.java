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

    @JkDoc("If true, Spring Milestone or Snapshot Repository will be used to fetch non release version of spring modules")
    public boolean autoSpringRepo = true;

    private final JkPluginJava java;

    /**
     * Right after to be instantiated, plugin instances are likely to configured by the owning build.
     * Therefore, every plugin members that are likely to be configured by the owning build must be
     * initialized in the constructor.
     */
    protected JkPluginSpringboot(JkRun run) {
        super(run);
        java = run.getPlugins().get(JkPluginJava.class);
    }

    @Override
    @JkDoc("Modifies the Java project from Java plugin in such this project produces a SpringBoot jar as the main artifact.")
    protected void activate() {
        activate(java.project());
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

        // add original jar artifact
        JkArtifactId original = JkArtifactId.of("original", "jar");
        Path originalPath = maker.getArtifactPath(original);
        JkArtifactId mainArtifactId = maker.getMainArtifactId();
        Runnable originalJarCreator = maker.getRunnable(mainArtifactId);
        maker.defineArtifact(original, originalJarCreator);

        // define bootable jar as main artifact
        JkVersion loaderVersion = versionProvider.getVersionOf(JkSpringModules.Boot.LOADER);
        Path bootloader = maker.getDependencyResolver().getRepos()
                .get(JkSpringModules.Boot.LOADER, loaderVersion.getValue());
        maker.defineArtifact(mainArtifactId, () -> {
            if (!Files.exists(originalPath)) {
                maker.makeArtifact(original);
            }
            final JkPathSequence nestedLibs = maker.fetchRuntimeDependencies(mainArtifactId);
            createBootJar(originalPath, nestedLibs, bootloader, maker.getMainArtifactPath(),
                    springbootVersion, mainClassName);
        });
    }

    public JkPluginJava java() {
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
        String className = mainClassName != null ? mainClassName : JkClassLoader.findMainClass(original);
        SpringbootPacker.of(libsToInclude, bootLoaderJar, className, springbootVersion).makeExecJar(original, targetJar);
    }

}
