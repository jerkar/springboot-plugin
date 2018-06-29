package org.jerkar.addin.springboot;

import org.jerkar.api.depmanagement.JkDependencyResolver;
import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkJavaDepScopes;
import org.jerkar.api.depmanagement.JkVersionedModule;
import org.jerkar.api.file.JkPathSequence;
import org.jerkar.api.file.JkPathTree;
import org.jerkar.api.java.JkClassLoader;
import org.jerkar.api.java.JkManifest;
import org.jerkar.api.system.JkException;
import org.jerkar.api.utils.JkUtilsIO;
import org.jerkar.api.utils.JkUtilsObject;
import org.jerkar.api.utils.JkUtilsPath;
import org.jerkar.api.utils.JkUtilsThrowable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

public class JkSpringbootPacker {

    private final JkDependencyResolver dependencyResolver;

    private final String loaderVersion;

    private JkVersionedModule versionedModule;

    private final JkManifest manifestToMerge;

    private final JkDependencySet dependencySet;

    private JkSpringbootPacker(JkDependencyResolver dependencyResolver, JkDependencySet dependencySet,
                               JkVersionedModule versionedModule,
            String loaderVersion, JkManifest manifestToMerge) {
        super();
        this.dependencyResolver = dependencyResolver;
        this.dependencySet = dependencySet;
        this.versionedModule = versionedModule;
        this.loaderVersion = loaderVersion;
        this.manifestToMerge = manifestToMerge;
    }

    public static final JkSpringbootPacker of(JkDependencyResolver resolver, JkDependencySet dependencySet,
                                              String bootloaderVersion) {
        return new JkSpringbootPacker(resolver, dependencySet, null, bootloaderVersion, null);
    }

    public JkSpringbootPacker module(JkVersionedModule versionedModule) {
        this.versionedModule = versionedModule;
        return this;
    }


    public void makeExecJar(Path original, Path target) {
        try {
            makeBootJarChecked(original, target);
        } catch (IOException e) {
            throw JkUtilsThrowable.unchecked(e);
        }
    }
    
    private Path bootinfJar(Path jar) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("jerkar-springboot");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Path bootClassDir = tempDir.resolve("dir/BOOT-INF/classes/");
        JkPathTree.ofZip(jar).copyTo(bootClassDir);
        Path tempZip = tempDir.resolve("boot-inf.jar");
        JkPathTree.ofZip(tempZip).merge(JkPathTree.of(tempDir).goTo("dir"));
        JkUtilsPath.deleteIfExists(bootClassDir);
        return tempZip;
    }

    private void makeBootJarChecked(Path original, Path target) throws IOException {

        JarWriter jarWriter = new JarWriter(target);

        // Add manifest
        String className = JkClassLoader.findMainClass(original);
        if (className == null) {
            throw new JkException("No class found with main method. Can't create executable jar.");
        }
        jarWriter.writeManifest(manifest(original, className).manifest());

        // Add original jar
        final Path bootinfJar = bootinfJar(original);
        JarFile bootinfJarFile = new JarFile(bootinfJar.toFile());
        jarWriter.writeEntries(bootinfJarFile);
        JkUtilsIO.closeQuietly(bootinfJarFile);

        // Add nested jars
        JkPathSequence libs = this.dependencyResolver.get(dependencySet, JkJavaDepScopes.RUNTIME);
        for (Path nestedJar : libs) {
            jarWriter.writeNestedLibrary("BOOT-INF/lib/", nestedJar);
        }

        // Add loader
        JkDependencySet bootloaderDep = JkDependencySet.of().and(JkSpringModules.Boot.LOADER, loaderVersion);
        JkPathSequence loaderJars = dependencyResolver.get(dependencySet.and(bootloaderDep));
        for (Path loaderJar : loaderJars) {
            JarFile loaderJarFile = new JarFile(loaderJar.toFile());
            jarWriter.writeEntries(loaderJarFile);
        }

        jarWriter.close();
        jarWriter.setExecutableFilePermission(target);
        JkUtilsPath.deleteFile(bootinfJar.getParent());
    }
    

    private JkManifest manifest(Path original, String startClassName) {
        JkManifest result = JkUtilsObject.firstNonNull(JkManifest.of(original), JkManifest.empty());
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
