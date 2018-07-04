import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkJavaDepScopes;
import org.jerkar.plugins.springboot.JkPluginSpringboot;
import org.jerkar.tool.JkImport;
import org.jerkar.tool.JkInit;
import org.jerkar.tool.builtins.java.JkJavaProjectBuild;

import static org.jerkar.plugins.springboot.JkSpringModules.Boot;

@JkImport("../org.jerkar.plugins.spring-boot/idea-output/classes")
class Build extends JkJavaProjectBuild {

    @Override
    protected void configurePlugins() {
        this.plugins().get(JkPluginSpringboot.class).setSpringbootVersion("2.0.2.RELEASE");
    }

    @Override
    protected void configure() {
        this.project().addDependencies(dependencies());
    }

    private JkDependencySet dependencies() {
	    return JkDependencySet.of()
		    .and(Boot.STARTER_WEB)
		    .and(Boot.STARTER_TEST, JkJavaDepScopes.TEST);
    }

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).doDefault();
    }
}
