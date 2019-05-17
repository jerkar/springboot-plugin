import org.jerkar.tool.JkInit;

class RunRelease {

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).release();
    }

}
