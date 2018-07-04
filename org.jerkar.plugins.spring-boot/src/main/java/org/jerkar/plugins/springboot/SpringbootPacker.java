package org.jerkar.plugins.springboot;

import org.jerkar.api.file.JkPathSequence;
import org.jerkar.api.file.JkPathTree;
import org.jerkar.api.java.JkManifest;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsIO;
import org.jerkar.api.utils.JkUtilsObject;
import org.jerkar.api.utils.JkUtilsPath;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

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
        Path path = JkPathTree.ofZip(original).goTo("META-INF").get("MANIFEST.MF");
        final JkManifest manifest = Files.exists(path) ? JkManifest.of(path) : JkManifest.empty();
        jarWriter.writeManifest(createManifest(manifest, mainClassNeme).manifest());

        // Add nested jars
        for (Path nestedJar : this.nestedLibs) {
            jarWriter.writeNestedLibrary("BOOT-INF/lib/", nestedJar);
        }

        // Add original jar
        writeClasses(original, target);

        // Add loader
        jarWriter.writeLoaderClasses(bootLaderJar.toUri().toURL());

        jarWriter.close();
        jarWriter.setExecutableFilePermission(target);
    }

    private void writeClasses(Path original, Path target) {
        Path tempDir = JkUtilsPath.createTempDirectory("jkspringboot");
        Path bootclass = tempDir.resolve("BOOT-INF/classes");
        //JkPathTree targetPathTree = JkPathTree.ofZip(target); // .goTo("/BOOT-INF/classes");
        JkPathTree.ofZip(original).copyTo(bootclass);
        System.out.println("---------------------------- " + tempDir);
        //JkUtilsPath.deleteIfExists(target);
        JkUtilsFile.zip(tempDir, target);
       // JkPathTree.of(tempDir).zipTo(target);
     //   JkPathTree.of(tempDir).deleteContent().deleteRoot();
       // targetPathTree.merge(originalPathTree);
        System.out.println("------------------------" + target);
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
