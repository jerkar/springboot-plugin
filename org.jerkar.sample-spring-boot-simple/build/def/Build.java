import org.jerkar.addin.springboot.JkSpringModules.Boot;
import org.jerkar.addin.springboot.JkSpringModules.Data;
import org.jerkar.addin.springboot.JkSpringModules.Fwk;
import org.jerkar.addin.springboot.JkSpringModules.Mobile;
import org.jerkar.addin.springboot.JkSpringModules.Security;
import org.jerkar.addin.springboot.JkSpringbootBuild;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkPopularModules;
import org.jerkar.tool.JkInit;

/**
 * @author djeang
 * @formatter:off
 */
//@JkImport({ "org.jerkar:addin-spring-boot:0.1-SNAPSHOT"})
class Build extends JkSpringbootBuild {

    public static void main(String[] args) {
	JkInit.instanceOf(Build.class, args, "-tests.output").doDefault();
    }

    @Override
    protected JkDependencies dependencies() {
	return JkDependencies.builder()
		.on(Boot.STARTER)
		.on(Boot.STARTER_TEST, TEST).build();
    }
}
