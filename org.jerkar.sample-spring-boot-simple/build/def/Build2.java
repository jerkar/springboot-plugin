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
public class Build2 extends JkSpringbootBuild {

    public static void main(String[] args) {
	JkInit.instanceOf(Build2.class, args, "-tests.output").doDefault();
    }

    @Override
    protected JkDependencies dependencies() {
	return JkDependencies.builder()
		.on(Boot.STARTER)
		.on(Fwk.JDBC)
		.on(Data.MONGODB)
		.on(Data.COMMONS)
		.on(Security.CORE)
		.on(Mobile.DEVICE)
		.on(JkPopularModules.GUAVA, "18.0")
		.on(Boot.STARTER_TEST, TEST).build();
    }
}
