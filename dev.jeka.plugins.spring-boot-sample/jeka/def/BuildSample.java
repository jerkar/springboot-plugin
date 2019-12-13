import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkImport;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import dev.jeka.plugins.springboot.JkPluginSpringboot;

import static dev.jeka.core.api.depmanagement.JkJavaDepScopes.TEST;

@JkImport("dev.jeka:springboot-plugin:2.0.1.RELEASE")
class BuildSample extends JkCommands {

    private final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    private final JkPluginSpringboot springbootPlugin = getPlugin(JkPluginSpringboot.class);

    @Override
    protected void setup() {
        springbootPlugin.springbootVersion = "2.0.3.RELEASE";
        javaPlugin.getProject().addDependencies(JkDependencySet.of()
                .and("org.springframework.boot:spring-boot-starter-web")
                .and("org.springframework.boot:spring-boot-starter-data-jpa")
                .and("org.springframework.boot:spring-boot-starter-data-rest")
                .and("org.springframework.boot:spring-boot-starter-test", TEST)
        );
    }

    // Clean, compile, test and generate springboot application jar
    public static void main(String[] args) {
        JkInit.instanceOf(BuildSample.class, args).javaPlugin.clean().pack();
    }

}
