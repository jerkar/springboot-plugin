import static org.jerkar.addin.springboot.JkSpringModules.*;
import org.jerkar.addin.springboot.JkSpringbootBuild;
import org.jerkar.addin.springboot.JkSpringbootVersionManagement;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.tool.JkImport;
import org.jerkar.tool.JkInit;

/**
 * @author djeang
 * @formatter:off
 */
@JkImport({ "org.jerkar:addin-spring-boot:1.3.1.0"})
class Build extends JkSpringbootBuild {

    public static void main(String[] args) {
	JkInit.instanceOf(Build.class, args, "-tests.output").doDefault();
    }
    
    @Override
    protected JkSpringbootVersionManagement versionManagement() {
        return JkSpringbootVersionManagement.v1_3_0();
    }
    

    @Override
    protected JkDependencies dependencies() {
	return JkDependencies.builder()
		.on(Boot.STARTER)
		.onIf(true, Boot.STARTER_SECURITY)
		.on(Boot.STARTER_TEST, TEST).build();
    }
}
