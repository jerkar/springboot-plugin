import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.depmanagement.JkScope;
import dev.jeka.core.tool.JkCommandSet;
import dev.jeka.core.tool.JkDefClasspath;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.intellij.JkPluginIntellij;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import dev.jeka.plugins.springboot.JkPluginSpringboot;
import dev.jeka.plugins.springboot.JkSpringModules.Boot;

import java.nio.file.Path;


@JkDefClasspath("dev.jeka:springboot-plugin:2.4.0.RC6")  // Add Springboot plugin to build classpath
@JkDefClasspath("com.jcraft:jsch:0.1.55")  // SSH library to deploy on remote hosts
class ApplicationBuild2 extends JkCommandSet {

    private final JkPluginJava java = getPlugin(JkPluginJava.class);

    private final JkPluginSpringboot springboot = getPlugin(JkPluginSpringboot.class);

    @JkDoc("The host to deploy Springboot application on.")
    public String host = "your.host";

    @Override
    protected void setup() {
        springboot.setSpringbootVersion("2.2.6.RELEASE");
        java.getProject()
            .getDependencyManagement()
                .addDependencies(JkDependencySet.of()
                    .and("org.springframework.boot:spring-boot-starter-web")
                    .and("org.springframework.boot:spring-boot-starter-data-jpa")
                    .and("org.springframework.boot:spring-boot-starter-data-rest")
                    .and("com.h2database:h2:1.4.200")
                    .and("com.google.guava:guava:23.0")
                    .and("org.springframework.boot:spring-boot-starter-test", JkScope.TEST)
                        .withLocalExclusions("org.junit.vintage:junit-vintage-engine")
                );
    }

    public void cleanPack() {
        clean(); java.test(); springboot.createBootJar();
    }

    public void deploy() {
        Path jar = java.getProject().getPublication().getArtifactProducer().getMainArtifactPath();
        this.copyToRemoteSsh(jar, "/usr/local");
        this.restartSpringbootOnRemoteSsh();
    }

    // Clean, compile, test and generate springboot application jar
    public static void main(String[] args) {
        JkInit.instanceOf(ApplicationBuild2.class, args).cleanPack();
    }




    private void copyToRemoteSsh(Path jar, String remotePath) {
    }

    private void restartSpringbootOnRemoteSsh() {
    }


}
