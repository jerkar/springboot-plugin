import org.jerkar.tool.JkInit;

class RunPublish {

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).javaPlugin.publish();
    }

}