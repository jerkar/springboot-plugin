import static org.jerkar.addin.springboot.JkSpringModules.*;
import org.jerkar.addin.springboot.JkSpringbootBuild;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.tool.JkImport;
import org.jerkar.tool.JkInit;

/**
 * @author djeang
 * @formatter:off
 */
@JkImport({ "org.jerkar:addin-spring-boot:1.2.7.0"})
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
