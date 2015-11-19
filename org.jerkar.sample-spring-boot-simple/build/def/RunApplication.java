import org.jerkar.tool.JkInit;

class RunApplication {

    public static void main(String[] args) {
	Build build = JkInit.instanceOf(Build.class, args);
	build.doDefault();
	build.runJar();
    }

}
