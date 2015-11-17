import java.io.File;

import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkRepo;
import org.jerkar.api.depmanagement.JkRepos;
import org.jerkar.api.depmanagement.JkScope;
import org.jerkar.api.file.JkFileTree;
import org.jerkar.api.file.JkPath;
import org.jerkar.api.file.JkZipper;
import org.jerkar.api.java.JkManifest;
import org.jerkar.api.system.JkLog;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsString;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

class _Build extends JkJavaBuild {

    private static final JkScope SPRING_BOOT_LOADER = JkScope.of("springBootLoader");

    private static final String SPRING_BOOT_GROUP = "org.springframework.boot";

    private static final String SPRING_GROUP = "org.springframework";

    private static final String SPRING_VERSION = "4.2.3.BUILD-SNAPSHOT";

    private static final String SPRING_BOOT_VERSION = "1.3.0.BUILD-SNAPSHOT";

    private static final String SPRING_BOOTLOADER_VERSION = "1.2.7.RELEASE";

    /**
     * @formatter:off
     */
    @Override
    protected JkDependencies dependencies() {
	return JkDependencies.builder()
		.on("org.yaml", "snakeyaml", "1.16", RUNTIME)
		.on("aopalliance", "aopalliance", "1.0", COMPILE)
		.on(springframeworkDeps(SPRING_VERSION))
		.on(springbootDeps(SPRING_BOOT_VERSION, SPRING_BOOTLOADER_VERSION))
		.on(loggingDeps())
		.on(testDeps()).build();
    }

    private JkDependencies springframeworkDeps(String version) {
	return JkDependencies.builder()
		.on(SPRING_GROUP, "spring-aop", version, COMPILE)
		.on(SPRING_GROUP, "spring-test", version, TEST)
		.on(SPRING_GROUP, "spring-beans", version, COMPILE)
		.on(SPRING_GROUP, "spring-core", version, COMPILE)
		.on(SPRING_GROUP, "spring-context", version, COMPILE).build();
    }

    private JkDependencies springbootDeps(String bootVersion, String loaderVersion) {
	return JkDependencies.builder()
		.on(SPRING_BOOT_GROUP, "spring-boot", bootVersion, COMPILE)
		.on(SPRING_BOOT_GROUP, "spring-boot-autoconfigure", bootVersion, COMPILE)
		.on(SPRING_BOOT_GROUP, "spring-boot-starter-test", bootVersion, TEST)
		.on(SPRING_BOOT_GROUP, "spring-boot-starter", bootVersion, COMPILE)
		.on(SPRING_BOOT_GROUP, "spring-boot-starter-logging", bootVersion, COMPILE)
		.on(SPRING_BOOT_GROUP, "spring-boot-loader", loaderVersion, SPRING_BOOT_LOADER).build();
    }

    private JkDependencies testDeps() {
	return JkDependencies.builder()
		.on("org.hamcrest", "hamcrest-core", "1.3", TEST)
		.on("org.hamcrest", "hamcrest-library", "1.3", TEST)
		.on("junit", "junit", "4.12", TEST)
		.on("org.mockito", "mockito-core", "1.10.19", TEST)
		.on("org.objenesis", "objenesis", "2.1", TEST).build();
    }

    private JkDependencies loggingDeps() {
   	return JkDependencies.builder()
   		.on("org.slf4j", "slf4j-api", "1.7.12", COMPILE)
		.on("org.slf4j", "jul-to-slf4j", "1.7.12", COMPILE)
		.on("org.slf4j", "jcl-over-slf4j", "1.7.12", COMPILE)
		.on("ch.qos.logback", "logback-core", "1.1.3", COMPILE)
		.on("ch.qos.logback", "logback-classic", "1.1.3", COMPILE).build();
       }

    @Override
    protected JkRepos downloadRepositories() {
	return JkRepo.mavenCentral().asRepos();
    }

    @Override
    public void pack() {
	super.pack();
	JkLog.startln("Creating Spring boot jar");
	File original = new File(this.packer().jarFile().getParentFile(), originalJarName());
	File target = packer().jarFile();
	JkUtilsFile.copyFile(target, original);
	makeBootJar(original, target);
	JkLog.done();
    }

    public void makeBootJar(File original, File target) {

	String archiveName = JkUtilsString.substringBeforeLast(target.getName(), ".");
	JkFileTree springBootArchiveDir = JkFileTree.of(ouputDir(archiveName));

	// Add libs
	JkPath libs = this.dependencyResolver().get(JkJavaBuild.RUNTIME);
	springBootArchiveDir.from("lib").importFiles(libs);

	// Add original jar
	JkZipper.unzip(original, springBootArchiveDir.root());

	// Add loader
	JkPath loaderLibs = dependencyResolver().get(SPRING_BOOT_LOADER);
	JkZipper.unzip(loaderLibs, springBootArchiveDir.root());

	// Create manifest
	springBootArchiveDir.include("META-INF/**").deleteAll();
	manifest().writeTo(springBootArchiveDir.file("META-INF/MANIFEST.MF"));

	// Create final jar
	springBootArchiveDir.zip().to(target);
	JkUtilsFile.deleteDir(springBootArchiveDir.root());
    }




    private JkManifest manifest() {
	return JkManifest.empty()
		.addMainClass("org.springframework.boot.loader.JarLauncher")
		.addContextualInfo()
		.addMainAttribute("Spring-Boot-Version", SPRING_BOOT_VERSION);


    }

    private String originalJarName() {
	String name = this.packer().jarFile().getName();
	String beforeExt = JkUtilsString.substringBeforeLast(name, ".");
	String ext = JkUtilsString.substringAfterLast(name, ".");
	return beforeExt + "-original." + ext;
    }

    public static void main(String[] args) {
	JkLog.verbose(false);
	new _Build().doPack();
    }

}
