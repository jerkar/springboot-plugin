import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.depmanagement.JkScope;
import dev.jeka.core.tool.*;
import dev.jeka.core.tool.builtins.intellij.JkPluginIntellij;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import dev.jeka.plugins.springboot.JkPluginSpringboot;
import dev.jeka.plugins.springboot.JkSpringModules.Boot;


@JkDefClasspath("../dev.jeka.plugins.spring-boot/jeka/output/dev.jeka.springboot-plugin.jar")
class ApplicationBuild extends JkCommandSet {

    private final JkPluginJava java = getPlugin(JkPluginJava.class);

    private final JkPluginSpringboot springboot = getPlugin(JkPluginSpringboot.class);

    @Override
    protected void setup() {
        springboot.setSpringbootVersion("2.2.4.RELEASE");
        java.getProject().getJarProduction().getDependencyManagement().addDependencies(JkDependencySet.of()
            .and(Boot.STARTER_WEB)  // Same as .and("org.springframework.boot:spring-boot-starter-web")
            .and(Boot.STARTER_DATA_JPA)
            .and(Boot.STARTER_DATA_REST)
            .and("com.h2database:h2:1.4.200")
            .and("com.google.guava:guava:23.0")
            .and(Boot.STARTER_TEST, JkScope.TEST)
        );
    }

    public void cleanPack() {
        clean(); springboot.createBootJar();
    }

    public void run() {
        springboot.run();
    }

    public void iml() {
        getPlugin(JkPluginIntellij.class).iml();
    }

    // Clean, compile, test and generate springboot application jar
    public static void main(String[] args) {
        JkInit.instanceOf(ApplicationBuild.class, args).cleanPack();
    }

}
