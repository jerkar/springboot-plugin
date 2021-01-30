import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.java.testing.JkTestSelection;
import dev.jeka.core.tool.JkCommandSet;
import dev.jeka.core.tool.JkDefClasspath;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import dev.jeka.plugins.springboot.JkPluginSpringboot;

import static dev.jeka.core.api.depmanagement.JkScope.TEST;

@JkDefClasspath("dev.jeka:springboot-plugin:${version}")
class Build extends JkClass {

    private final JkPluginSpringboot springboot = getPlugin(JkPluginSpringboot.class);

    @Override
    protected void setup() {
        springboot.setSpringbootVersion("2.3.1.RELEASE");
        springboot.javaPlugin().getProject().simpleFacade()
            .addDependencies(JkDependencySet.of()
                .and("org.springframework.boot:spring-boot-starter-web")
                .and("org.springframework.boot:spring-boot-starter-test", TEST)
                    .withLocalExclusions("org.junit.vintage:junit-vintage-engine"));
    }

    @JkDoc("Cleans, tests and creates bootable jar.")
    public void cleanPack() {
        clean(); springboot.javaPlugin().pack();
    }

    // Clean, compile, test and generate springboot application jar
    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).cleanPack();
    }

}
