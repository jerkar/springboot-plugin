import org.jerkar.tool.JkInit;

class RunDoPublish {

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).maker().publish();
    }

}
