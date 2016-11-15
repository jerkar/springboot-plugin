import java.io.File;

import org.jerkar.api.depmanagement.JkModuleDependency;
import org.jerkar.api.depmanagement.JkRepos;
import org.jerkar.api.system.JkLog;
import org.jerkar.api.tooling.JkCodeWriterForBuildClass;
import org.jerkar.api.tooling.JkMvn;
import org.jerkar.api.tooling.JkPom;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.tool.JkInit;

/**
 * Generates Java code in the console to copy/paste in VersionX_X_X classes. 
 * 
 * @author Jerome Angibaud
 */
class CodeGenRunner {
    
    static String version = "1.4.2.RELEASE";
    
    public static void main(String[] args) {
       JkInit.instanceOf(Build.class);
       JkLog.verbose(true);
       JkModuleDependency pomDep = JkModuleDependency.of("org.springframework.boot", "spring-boot-starter-parent", version)
               .ext("pom");
       File pom = JkRepos.mavenCentral().get(pomDep);
       File effectivePom = JkUtilsFile.tempFile("pom", "pom");
       JkMvn.of(JkUtilsFile.workingDir(), "-f", pom.getPath(), "help:effective-pom", "-Doutput=" + effectivePom.getPath() ).run();
       JkPom jkPom = JkPom.of(effectivePom);
       effectivePom.delete();
       JkCodeWriterForBuildClass gen = new JkCodeWriterForBuildClass();
       gen.dependencyExclusions = jkPom.dependencyExclusion();
       gen.versionProvider = jkPom.versionProvider();
       System.out.println(gen.wholeClass());
    }

}
