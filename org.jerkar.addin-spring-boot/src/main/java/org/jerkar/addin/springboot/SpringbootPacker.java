package org.jerkar.addin.springboot;

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
import java.util.jar.Manifest;

class SpringbootPacker {

    private final JkPathSequence nestedLibs;

    private final Path bootLaderJar;

    private final JkManifest manifestToMerge;

    private final String mainClassNeme;

    private SpringbootPacker(JkPathSequence nestedLibs, Path loader, String mainClassNeme, JkManifest manifestToMerge) {
        super();
        this.nestedLibs = nestedLibs;
        this.bootLaderJar = loader;
        this.manifestToMerge = manifestToMerge;
        this.mainClassNeme = mainClassNeme;
    }

    public static final SpringbootPacker of(JkPathSequence nestedLibs, Path loader, String mainClassName) {
        return new SpringbootPacker(nestedLibs, loader, mainClassName, null);
    }

    public void makeExecJar(Path original, Path target) {
        try {
            makeBootJarChecked(original, target);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void makeBootJarChecked(Path original, Path target) throws IOException {

        JarWriter jarWriter = new JarWriter(target);

        // Manifest
        Path path = JkPathTree.ofZip(original).goTo("META-INF").get("manifest.mf");
        final JkManifest manifest = Files.exists(path) ? JkManifest.of(path) : JkManifest.empty();
        jarWriter.writeManifest(createManifest(manifest, mainClassNeme).manifest());

        // Add original jar
        jarWriter.writeEntries(new JarFile(original.toFile()));

        // Add nested jars
        for (Path nestedJar : this.nestedLibs) {
            jarWriter.writeNestedLibrary("BOOT-INF/lib/", nestedJar);
        }

        // Add loader
        jarWriter.writeLoaderClasses(bootLaderJar.toUri().toURL());

        jarWriter.close();
        jarWriter.setExecutableFilePermission(target);
    }
    
    
    private JkManifest createManifest(JkManifest original, String startClassName) {
        JkManifest result = JkUtilsObject.firstNonNull(original, JkManifest.empty());
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
