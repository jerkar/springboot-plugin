import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.tool.*;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import dev.jeka.plugins.springboot.JkPluginSpringboot;
import dev.jeka.plugins.springboot.JkSpringModules.Boot;

import static dev.jeka.core.api.depmanagement.JkJavaDepScopes.TEST;

@JkDefClasspath("../dev.jeka.plugins.spring-boot/jeka/output/dev.jeka.springboot-plugin.jar")
class ApplicationBuild extends JkCommandSet {

    private final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    private final JkPluginSpringboot springbootPlugin = getPlugin(JkPluginSpringboot.class);

    @Override
    protected void setup() {
        springbootPlugin.setSpringbootVersion("2.2.4.RELEASE");
        javaPlugin.getProject().addDependencies(JkDependencySet.of()
                .and(Boot.STARTER_WEB)  // Same as .and("org.springframework.boot:spring-boot-starter-web")
                .and(Boot.STARTER_DATA_JPA)
                .and(Boot.STARTER_DATA_REST)
                .and(Boot.STARTER_TEST, TEST)
                .and("com.h2database:h2:1.4.200")
                .and("com.google.guava:guava:23.0")
        );
    }

    public void cleanPack() {
        clean(); javaPlugin.pack();
    }

    public void run() {
        springbootPlugin.run();
    }

    // Clean, compile, test and generate springboot application jar
    public static void main(String[] args) {
        JkInit.instanceOf(ApplicationBuild.class, args).cleanPack();
    }

}
