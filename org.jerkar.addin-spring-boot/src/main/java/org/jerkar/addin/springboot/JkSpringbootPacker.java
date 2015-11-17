package org.jerkar.addin.springboot;

import java.io.File;
import java.util.zip.Deflater;

import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkDependencyResolver;
import org.jerkar.api.depmanagement.JkVersionedModule;
import org.jerkar.api.file.JkFileTree;
import org.jerkar.api.file.JkPath;
import org.jerkar.api.file.JkZipper;
import org.jerkar.api.file.JkZipper.JkCompressionLevel;
import org.jerkar.api.file.JkZipper.JkCompressionMethod;
import org.jerkar.api.java.JkManifest;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsObject;
import org.jerkar.api.utils.JkUtilsString;
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

	String archiveName = JkUtilsString.substringBeforeLast(target.getName(), ".");
	JkFileTree springBootArchiveDir = JkFileTree.of(target.getParentFile()).from(archiveName);

	// Add libs
	JkPath libs = this.dependencyResolver.get(JkJavaBuild.RUNTIME);
	springBootArchiveDir.from("lib").importFiles(libs);

	// Add original jar
	JkZipper.unzip(original, springBootArchiveDir.root());
	
	// Find main class
	String className = JkSpringbootUtils.findMainClass(springBootArchiveDir.root());

	// Add loader
	JkDependencies bootloaderDep = JkDependencies.builder().on(JkSpringModules.Boot.LOADER, loaderVersion).build();
	JkPath loaderLibs = dependencyResolver.withDependencies(bootloaderDep).get();
	JkZipper.unzip(loaderLibs, springBootArchiveDir.root());

	// Create manifest
	springBootArchiveDir.include("META-INF/**").deleteAll();
	manifest(original, className).writeTo(springBootArchiveDir.file("META-INF/MANIFEST.MF"));

	// Create final jar
	springBootArchiveDir.zip().with(JkCompressionLevel.DEFAULT_COMPRESSION)
		.with(JkCompressionMethod.STORED).to(target);
	JkUtilsFile.deleteDir(springBootArchiveDir.root());
    }
    
    private JkManifest manifest(File original, String startClassName) {
	JkManifest result = JkUtilsObject.firstNonNull(JkManifest.ofArchive(original), JkManifest.empty());
	result.addMainClass("org.springframework.boot.loader.JarLauncher");
	result.addMainAttribute("Start-Class" , startClassName);
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
