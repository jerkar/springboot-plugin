package dev.jeka.plugins.springboot;

import dev.jeka.core.api.depmanagement.*;
import dev.jeka.core.api.file.JkPathSequence;
import dev.jeka.core.api.java.JkClassLoader;
import dev.jeka.core.api.java.JkJavaProcess;
import dev.jeka.core.api.java.JkUrlClassLoader;
import dev.jeka.core.api.java.project.JkJavaProject;
import dev.jeka.core.api.java.project.JkJavaProjectMaker;
import dev.jeka.core.api.system.JkException;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.tooling.JkPom;
import dev.jeka.core.api.utils.JkUtilsIO;
import dev.jeka.core.api.utils.JkUtilsReflect;
import dev.jeka.core.api.utils.JkUtilsString;
import dev.jeka.core.tool.JkCommandSet;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkDocPluginDeps;
import dev.jeka.core.tool.JkPlugin;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import dev.jeka.core.tool.builtins.scaffold.JkPluginScaffold;

import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@JkDoc("Provides enhancement to Java plugin in order to produce a startable Springboot jar for your application.\n" +
        "The main produced artifact is the springboot one (embedding all dependencies) while the artifact classified as 'original' stands for the vanilla jar.\n" +
        "Dependency versions are resolved against BOM provided by Spring Boot team according Spring Boot version you use.")
@JkDocPluginDeps(JkPluginJava.class)
public final class JkPluginSpringboot extends JkPlugin {

    private static final String SPRINGBOOT_APPLICATION_ANNOTATION_NAME =
            "org.springframework.boot.autoconfigure.SpringBootApplication";

    @JkDoc("Version of Spring Boot version used to resolve dependency versions.")
    private String springbootVersion = "2.0.3.RELEASE";

    @JkDoc("Class name holding main method to start Spring Boot. If null, Jerkar will try to guess it at build time.")
    public String mainClassName;

    @JkDoc("If true, Spring Milestone or Snapshot Repository will be used to fetch non release version of spring modules")
    public boolean autoSpringRepo = true;

    @JkDoc("command arg line to pass to springboot for #run method (e.g. '--server.port=8083 -Dspring.profiles.active=prod'")
    public String runArgs;

    private final JkPluginJava java;

    /**
     * Right after to be instantiated, plugin instances are likely to configured by the owning build.
     * Therefore, every plugin members that are likely to be configured by the owning build must be
     * initialized in the constructor.
     */
    protected JkPluginSpringboot(JkCommandSet commandSet) {
        super(commandSet);
        java = commandSet.getPlugins().get(JkPluginJava.class);
    }

    @Override
    protected String getLowestJekaCompatibleVersion() {
        return "0.8.20.RELEASE";
    }

    public void setSpringbootVersion(String springbootVersion) {
        this.springbootVersion = springbootVersion;
    }

    @Override
    @JkDoc("Modifies the Java project from Java plugin in such this project produces a SpringBoot jar as the main artifact.")
    protected void activate() {
        activate(java.getProject());
    }

    @JkDoc("Run Springboot application from the generated jar")
    public void run() {
        JkJavaProjectMaker maker =  this.javaPlugin().getProject().getMaker();
        JkArtifactId mainArtifactId = maker.getMainArtifactId();
        maker.makeMissingArtifacts(mainArtifactId);
        Path mainArtifactFile = maker.getMainArtifactPath();
        JkJavaProcess process = JkJavaProcess.of();
        String[] args = new String[0];
        if (!JkUtilsString.isBlank(this.runArgs)) {
            args = JkUtilsString.translateCommandline(this.runArgs);
        }
        JkJavaProcess.of().runJarSync(mainArtifactFile, args);
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
            final JkPathSequence embeddedJars = maker.fetchRuntimeDependencies(mainArtifactId);
            createBootJar(originalPath, embeddedJars, bootloader, maker.getMainArtifactPath(),
                    springbootVersion, mainClassName);
        });

        // Add template build class to scaffold
        if (this.getCommandSet().getPlugins().hasLoaded(JkPluginScaffold.class)) {
            JkPluginScaffold scaffold = this.getCommandSet().getPlugins().get(JkPluginScaffold.class);
            String code = JkUtilsIO.read(JkPluginSpringboot.class.getResource("Build.java.snippet"));
            scaffold.getScaffolder().setCommandClassCode(code);
        }
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
        JkLog.info("Springboot dependency versions will be resolved from " + pomFile);
        return pom.getVersionProvider();
    }

    public static void createBootJar(Path original, JkPathSequence libsToInclude, Path bootLoaderJar, Path targetJar,
                                     String springbootVersion, String mainClassName) {
        JkClassLoader classLoader = JkUrlClassLoader.of(original, ClassLoader.getSystemClassLoader().getParent())
                .toJkClassLoader();
        List<String> mainClasses = classLoader.findClassesHavingMainMethod();
        List<String> classWithSpringbootAppAnnotation = classLoader.findClassesMatchingAnnotations(
                annotationNames -> annotationNames.contains(SPRINGBOOT_APPLICATION_ANNOTATION_NAME));
        for (String name : mainClasses) {
            if (classWithSpringbootAppAnnotation.contains(name)) {
                SpringbootPacker.of(libsToInclude, bootLoaderJar, name,
                        springbootVersion).makeExecJar(original, targetJar);
                return;
            }
        }
        throw new JkException("No @SpringBootApplication class with main method found.");
    }

}
