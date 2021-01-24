import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.api.tooling.JkGitWrapper;
import dev.jeka.core.tool.JkCommandSet;
import dev.jeka.core.tool.JkEnv;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import static dev.jeka.core.api.depmanagement.JkScope.PROVIDED;

class Build extends JkCommandSet {

    final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    @JkEnv("OSSRH_USER")
    public String ossrhUser;

    @JkEnv("OSSRH_PWD")
    public String ossrhPwd;

    @Override
    protected void setup() {
        JkGitWrapper git = JkGitWrapper.of(getBaseDir());
        String version = git.getVersionFromTags();
        javaPlugin.getProject()
            .getConstruction()
                .getDependencyManagement().addDependencies(JkDependencySet.of()
                    .andFile(JkLocator.getJekaJarPath(), PROVIDED)).__
                .getCompilation()
                    .getLayout()
                        .includeSourceDirsInResources().__
                    .setJavaVersion(JkJavaVersion.V8)
                .getResourceProcessor()
                    .addInterpolator("**/Build.java", "${version}", version).__.__.__
            .getPublication()
                .setModuleId("dev.jeka:springboot-plugin")
                .setVersion(version)
                .setRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUser, ossrhPwd))
                .getMavenPublication()
                    .getPomMetadata()
                        .addApache2License()
                        .getProjectInfo()
                            .setName("Jeka plugin for Spring Boot")
                            .setDescription("A Jeka plugin for Spring boot application")
                            .setUrl("https://github.com/jerkar/spring-boot-plugin").__
                        .getScm()
                            .setUrl("https://github.com/jerkar/spring-boot-addin.git").__
                        .addGithubDeveloper("djeang", "djeangdev@yahoo.fr");
    }


    public void cleanPack() {
        clean(); javaPlugin.pack();
    }



    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).cleanPack();
    }

}