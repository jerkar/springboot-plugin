import dev.jeka.core.api.depmanagement.JkFileSystemDependency;
import dev.jeka.core.api.depmanagement.JkModuleId;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.api.tooling.JkGitWrapper;
import dev.jeka.core.tool.JkClass;
import dev.jeka.core.tool.JkEnv;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.JkPlugin;
import dev.jeka.core.tool.builtins.git.JkPluginGit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import dev.jeka.core.tool.builtins.repos.JkPluginGpg;

import java.util.Optional;

class Build extends JkClass {

    final JkModuleId SPRINGBOOT_PARENT = JkModuleId.of("org.springframework.boot","spring-boot-starter-parent");

    final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    final JkPluginGpg gpgPlugin = getPlugin((JkPluginGpg.class));

    final JkPluginGit gitPlugin = getPlugin(JkPluginGit.class);

    @JkEnv("OSSRH_USER")
    public String ossrhUser;

    @JkEnv("OSSRH_PWD")
    public String ossrhPwd;

    @Override
    protected void setup() {
        String version = version();
        javaPlugin.getProject().simpleFacade()
                .setJavaVersion(JkJavaVersion.V8)
                .setCompileDependencies(deps -> deps
                        .andFiles(JkLocator.getJekaJarPath())
                )
                .setRuntimeDependencies(deps -> deps
                        .minus(JkFileSystemDependency.of(JkLocator.getJekaJarPath()))
                );
        javaPlugin.getProject()
            .getConstruction()
                .getManifest()
                    .addMainAttribute(JkPlugin.MANIFEST_LOWEST_JEKA_COMPATIBLE_VERSION_ENTRY, "0.9.10.RELEASE")
                    .addMainAttribute(JkPlugin.MANIFEST_BREAKING_CHANGE_URL_ENTRY,
                            "https://raw.githubusercontent.com/jerkar/springboot-plugin/master/breaking_versions.txt")
                    .__.__
            .getPublication()
                .getMaven()
                    .setModuleId("dev.jeka:springboot-plugin")
                    .setVersion(version)
                    .setRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUser, ossrhPwd,
                            gpgPlugin.get().getSigner("")))
                    .getPomMetadata()
                        .addApache2License()
                        .getProjectInfo()
                            .setName("Jeka plugin for Spring Boot")
                            .setDescription("A Jeka plugin for Spring boot application")
                            .setUrl("https://github.com/jerkar/spring-boot-plugin").__
                        .getScm()
                            .setUrl("https://github.com/jerkar/spring-boot-addin.git").__
                        .addGithubDeveloper("djeang", "djeangdev@yahoo.fr").__.__
                .getPostActions()
                    .append(this::tagIfNeeded);
    }

    private String version() {
        String currentTagVersion = gitPlugin.getWrapper().getVersionFromTag();
        currentTagVersion = currentTagVersion.equals("HEAD-SNAPSHOT") ? "master-SNAPSHOT" : currentTagVersion;
        String releaseVersion = gitPlugin.getWrapper().extractSuffixFromLastCommitMessage("Release:");
        return Optional.ofNullable(releaseVersion).orElse(currentTagVersion);
    }



    private void tagIfNeeded() {
        JkGitWrapper git = gitPlugin.getWrapper();
        Optional.ofNullable(git.extractSuffixFromLastCommitMessage("Release:"))
                .ifPresent(version -> git.tagAndPush(version));
    }

    public void cleanPack() {
        clean(); javaPlugin.pack();
    }

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).cleanPack();
    }

}