import org.jerkar.tool.JkInit;
import org.jerkar.tool.builtins.eclipse.JkBuildPluginEclipse;

class RunEclipseGenerateFiles {

    public static void main(String[] args) {
	JkInit.instanceOf(Build.class, args, "eclipse#").pluginOf(JkBuildPluginEclipse.class).generateFiles();
    }

}
