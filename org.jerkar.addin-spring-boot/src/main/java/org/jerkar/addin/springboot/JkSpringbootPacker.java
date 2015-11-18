package org.jerkar.addin.springboot;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkDependencyResolver;
import org.jerkar.api.depmanagement.JkVersionedModule;
import org.jerkar.api.file.JkPath;
import org.jerkar.api.java.JkClassLoader;
import org.jerkar.api.java.JkManifest;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsObject;
import org.jerkar.api.utils.JkUtilsString;
import org.jerkar.api.utils.JkUtilsThrowable;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

public class JkSpringbootPacker {

    private final JkDependencyResolver dependencyResolver;

    private final String loaderVersion;

    private JkVersionedModule versionedModule;

    private final JkManifest manifestToMerge;

    private JkSpringbootPacker(JkDependencyResolver dependencyResolver, JkVersionedModule versionedModule,
	    String loaderVersion, JkManifest manifestToMerge) {
	super();
	this.dependencyResolver = dependencyResolver;
	this.versionedModule = versionedModule;
	this.loaderVersion = loaderVersion;
	this.manifestToMerge = manifestToMerge;
    }

    public static final JkSpringbootPacker of(JkDependencyResolver resolver, String bootloaderVersion) {
	return new JkSpringbootPacker(resolver, null, bootloaderVersion, null);
    }

    public JkSpringbootPacker module(JkVersionedModule versionedModule) {
	this.versionedModule = versionedModule;
	return this;
    }

    public File makeExecJar(File original) {
	File target = executableJar(original);
	makeBootJar(original, target);
	return target;
    }

    public void makeBootJar(File original, File target) {
	try {
	    makeBootJarChecked(original, target);
	} catch (IOException e) {
	    throw JkUtilsThrowable.unchecked(e);
	}
    }

    private void makeBootJarChecked(File original, File target) throws IOException {
	JarWriter jarWriter = new JarWriter(JkUtilsFile.createFileIfNotExist(target));

	// Add manifest
	String className = JkClassLoader.findMainClass(original);
	jarWriter.writeManifest(manifest(original, className).manifest());

	// Add original jar
	JarFile jarFile = new JarFile(original);
	jarWriter.writeEntries(jarFile);

	// Add nested jars
	JkPath libs = this.dependencyResolver.get(JkJavaBuild.RUNTIME);
	for (File nestedJar : libs) {
	    jarWriter.writeNestedLibrary("lib/", nestedJar);
	}

	// Add loader
	JkDependencies bootloaderDep = JkDependencies.builder().on(JkSpringModules.Boot.LOADER, loaderVersion).build();
	JkPath loaderJars = dependencyResolver.withDependencies(bootloaderDep).get();
	for (File loaderJar : loaderJars) {
	    JarFile loaderJarFile = new JarFile(loaderJar);
	    jarWriter.writeEntries(loaderJarFile);
	}

	jarWriter.close();

    }

    private JkManifest manifest(File original, String startClassName) {
	JkManifest result = JkUtilsObject.firstNonNull(JkManifest.ofArchive(original), JkManifest.empty());
	result.addMainClass("org.springframework.boot.loader.JarLauncher");
	result.addMainAttribute("Start-Class", startClassName);
	result.addContextualInfo();
	if (this.versionedModule != null) {
	    this.versionedModule.populateManifest(result);
	}
	if (this.manifestToMerge != null) {
	    result.merge(manifestToMerge);
	}
	return result;
    }

    private File executableJar(File original) {
	String name = JkUtilsString.substringBeforeLast(original.getName(), ".");
	String ext = JkUtilsString.substringAfterLast(original.getName(), ".");
	String execName = name + "-executable." + ext;
	return new File(original.getParentFile(), execName);
    }

}
