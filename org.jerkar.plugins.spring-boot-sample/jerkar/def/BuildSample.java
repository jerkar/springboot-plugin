import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkJavaDepScopes;
import org.jerkar.api.depmanagement.JkRepo;
import org.jerkar.plugins.springboot.JkPluginSpringboot;
import org.jerkar.tool.JkImport;
import org.jerkar.tool.JkInit;
import org.jerkar.tool.builtins.java.JkJavaProjectBuild;

import static org.jerkar.plugins.springboot.JkSpringModules.Boot;

//@JkImport("../org.jerkar.plugins.spring-boot/.idea/output/production")
@JkImport("org.jerkar.plugins:springboot:2.0-SNAPSHOT")
class BuildSample extends JkJavaProjectBuild {

    protected BuildSample() {
        this.getPlugins().get(JkPluginSpringboot.class).springbootVersion = "2.0.3.RELEASE";
    }

    @Override
    protected void setup() {
        project().addDependencies(dependencies());
    }

    private JkDependencySet dependencies() {
	    return JkDependencySet.of()
		    .and(Boot.STARTER_WEB)
	        .and(Boot.STARTER_TEST, JkJavaDepScopes.TEST);
    }

    @Override
    protected void postPluginSetup() {
        maker().setDependencyResolver(maker().getDependencyResolver().withRepos(JkRepo.ofMavenCentral()));
    }

    public static void main(String[] args) {
        JkInit.instanceOf(BuildSample.class, args).java().pack();
    }
}
