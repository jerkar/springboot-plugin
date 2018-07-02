
import static org.jerkar.addin.springboot.JkSpringModules.*;

import org.jerkar.addin.springboot.JkPluginSpringBoot;
import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkJavaDepScopes;
import org.jerkar.tool.JkImport;
import org.jerkar.tool.JkInit;
import org.jerkar.tool.builtins.java.JkJavaProjectBuild;

@JkImport("../org.jerkar.addin-spring-boot/build/output/classes")
class Build extends JkJavaProjectBuild {

    @Override
    protected void configurePlugins() {
        this.plugins().get(JkPluginSpringBoot.class).springbootVersion = "2.0.2.RELEASE";
    }

    @Override
    protected void configure() {
        this.project().setDependencies(this.project().getDependencies().and(dependencies()));
    }

    private JkDependencySet dependencies() {
	    return JkDependencySet.of()
		    .and(Boot.STARTER)
            .and( Boot.STARTER_SECURITY)
		    .and(Boot.STARTER_TEST, JkJavaDepScopes.TEST);
    }

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).java().showDependencies();
    }
}
