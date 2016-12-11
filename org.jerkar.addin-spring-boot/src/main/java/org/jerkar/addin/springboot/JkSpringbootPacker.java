package org.jerkar.addin.springboot;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkDependencyResolver;
import org.jerkar.api.depmanagement.JkVersionedModule;
import org.jerkar.api.file.JkFileTree;
import org.jerkar.api.file.JkPath;
import org.jerkar.api.java.JkClassLoader;
import org.jerkar.api.java.JkManifest;
import org.jerkar.api.system.JkLog;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsIO;
import org.jerkar.api.utils.JkUtilsObject;
import org.jerkar.api.utils.JkUtilsSystem;
import org.jerkar.api.utils.JkUtilsThrowable;
import org.jerkar.api.utils.JkUtilsZip;
import org.jerkar.tool.JkException;
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


    public void makeExecJar(File original, File target) {
        try {
            makeBootJarChecked(original, target);
        } catch (IOException e) {
            throw JkUtilsThrowable.unchecked(e);
        }
    }
    
    private File bootinfJar(File jar) {
        File tempDir = JkUtilsFile.createTempDir("jerkar-springboot");
        File bootClassDir = new File(tempDir, "dir/BOOT-INF/classes/");
        JkUtilsZip.unzip(jar, bootClassDir);
        File tempZip = new File(tempDir, "boot-inf.jar");
        JkFileTree.of(tempDir).from("dir").zip().to(tempZip);
        JkUtilsFile.deleteDir(bootClassDir);
        return tempZip;
    }

    private void makeBootJarChecked(File original, File target) throws IOException {
        JarWriter jarWriter = new JarWriter(JkUtilsFile.createFileIfNotExist(target));

        // Add manifest
        String className = JkClassLoader.findMainClass(original);
        if (className == null) {
            throw new JkException("No class found with main method. Can't create executable jar.");
        }
        jarWriter.writeManifest(manifest(original, className).manifest());

        // Add original jar
        final File bootinfJar = bootinfJar(original);
        JarFile bootinfJarFile = new JarFile(bootinfJar);
        jarWriter.writeEntries(bootinfJarFile);
        JkUtilsIO.closeQuietly(bootinfJarFile);

        // Add nested jars
        JkPath libs = this.dependencyResolver.get(JkJavaBuild.RUNTIME);
        for (File nestedJar : libs) {
            jarWriter.writeNestedLibrary("BOOT-INF/lib/", nestedJar);
        }

        // Add loader
        JkDependencies bootloaderDep = JkDependencies.builder().on(JkSpringModules.Boot.LOADER, loaderVersion).build();
        JkPath loaderJars = dependencyResolver.withDependencies(bootloaderDep).get();
        for (File loaderJar : loaderJars) {
            JarFile loaderJarFile = new JarFile(loaderJar);
            jarWriter.writeEntries(loaderJarFile);
        }

        jarWriter.close();
        jarWriter.setExecutableFilePermission(target);
        boolean deleteSuccess = JkUtilsFile.tryDeleteDir(bootinfJar.getParentFile());
        JkLog.warnIf(!deleteSuccess, "Can't delete " + bootinfJar.getParentFile() + " directory.");
    }
    
    

    private JkManifest manifest(File original, String startClassName) {
        JkManifest result = JkUtilsObject.firstNonNull(JkManifest.ofArchive(original), JkManifest.empty());
        result.addMainClass("org.springframework.boot.loader.JarLauncher");
        result.addMainAttribute("Start-Class", startClassName);
        result.addMainAttribute("Spring-Boot-Classes", "BOOT-INF/classes/");
        result.addMainAttribute("Spring-Boot-Lib", "BOOT-INF/lib/");

        result.addContextualInfo();
        if (this.versionedModule != null) {
            this.versionedModule.populateManifest(result);
        }
        if (this.manifestToMerge != null) {
            result.merge(manifestToMerge);
        }
        return result;
    }


}
