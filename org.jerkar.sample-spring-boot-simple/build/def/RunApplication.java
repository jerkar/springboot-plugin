import org.jerkar.tool.JkInit;

class RunApplication {

    public static void main(String[] args) {
	Build2 build = JkInit.instanceOf(Build2.class, args, "-verbose");
	build.doDefault();
	build.runJar();
    }

}
