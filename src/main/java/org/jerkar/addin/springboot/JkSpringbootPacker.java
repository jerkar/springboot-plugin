package org.jerkar.addin.springboot;

import static org.jerkar.addin.springboot.JkSpringbootModules.LOADER;

import java.io.File;

import org.jerkar.api.depmanagement.JkDependencyResolver;
import org.jerkar.api.depmanagement.JkModuleDependency;
import org.jerkar.api.depmanagement.JkVersion;
import org.jerkar.api.file.JkFileTree;
import org.jerkar.api.file.JkPath;
import org.jerkar.api.file.JkZipper;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsString;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

public class JkSpringbootPacker {
    
    private final JkDependencyResolver dependencyResolver;
    
    private final String loaderVersion;
    
    public JkSpringbootPacker(JkDependencyResolver dependencyResolver, String version) {
	super();
	this.dependencyResolver = dependencyResolver;
	this.loaderVersion = version;
    }


    public void makeBootJar(File original, File target) {

   	String archiveName = JkUtilsString.substringBeforeLast(target.getName(), ".");
   	JkFileTree springBootArchiveDir = JkFileTree.of(target.getParentFile()).from(archiveName);

   	// Add libs
   	JkPath libs = this.dependencyResolver.get(JkJavaBuild.RUNTIME);
   	springBootArchiveDir.from("lib").importFiles(libs);

   	// Add original jar
   	JkZipper.unzip(original, springBootArchiveDir.root());

   	// Add loader
   	JkModuleDependency loaderDep = JkModuleDependency.of(LOADER, loaderVersion);
   	JkPath loaderLibs = dependencyResolver.repos().get(loaderDep, true);
   	JkZipper.unzip(loaderLibs, springBootArchiveDir.root());

   	// Create manifest
   	springBootArchiveDir.include("META-INF/**").deleteAll();
   	manifest().writeTo(springBootArchiveDir.file("META-INF/MANIFEST.MF"));

   	// Create final jar
   	springBootArchiveDir.zip().to(target);
   	JkUtilsFile.deleteDir(springBootArchiveDir.root());
       }


}
