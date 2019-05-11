import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkJavaDepScopes;
import org.jerkar.plugins.springboot.JkPluginSpringboot;
import org.jerkar.tool.JkImport;
import org.jerkar.tool.JkImportRepo;
import org.jerkar.tool.JkInit;
import org.jerkar.tool.JkRun;
import org.jerkar.tool.builtins.java.JkPluginJava;

import static org.jerkar.plugins.springboot.JkSpringModules.Boot;

//@JkImport("../org.jerkar.plugins.spring-boot/.idea/output/production")
@JkImport("org.jerkar.plugins:springboot:2.0.0-SNAPSHOT")
@JkImportRepo("https://oss.sonatype.org/content/repositories/snapshots")
class BuildSample extends JkRun {

    private final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    private final JkPluginSpringboot springbootPlugin = getPlugin(JkPluginSpringboot.class);

    @Override
    protected void setup() {
        springbootPlugin.springbootVersion = "2.0.3.RELEASE";
        javaPlugin.getProject().addDependencies(JkDependencySet.of()
                .and(Boot.STARTER_WEB)
                .and(Boot.STARTER_TEST, JkJavaDepScopes.TEST)
        );
    }

    public static void main(String[] args) {
        JkInit.instanceOf(BuildSample.class, args).javaPlugin.clean().pack();
    }

}
