package org.jerkar.addin.springboot;

import org.jerkar.api.depmanagement.JkDependencyResolver;
import org.jerkar.api.depmanagement.JkDependencySet;
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

class SpringbootPacker {

    private final JkPathSequence nestedLibs;

    private final Path bootLaderJar;

    private final JkManifest manifestToMerge;

    private SpringbootPacker(JkPathSequence nestedLibs, Path loader, JkManifest manifestToMerge) {
        super();
        this.nestedLibs = nestedLibs;
        this.bootLaderJar = loader;
        this.manifestToMerge = manifestToMerge;
    }

    public static final SpringbootPacker of(JkPathSequence nestedLibs, Path loader) {
        return new SpringbootPacker(nestedLibs, loader, null);
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
        for (Path nestedJar : this.nestedLibs) {
            jarWriter.writeNestedLibrary("BOOT-INF/lib/", nestedJar);
        }

        // Add loader
        JkPathSequence loaderJars = nestedLibs.and(bootLaderJar);
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
        if (this.manifestToMerge != null) {
            result.merge(manifestToMerge);
        }
        return result;
    }


}
