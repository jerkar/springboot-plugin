# Springboot plugin for Jerkar

This provides a [Jerkar](http://project.jerkar.org) addin to build Spring Boot applications with minimal effort. <br/>
It honors the promise of using **Java and only Java all over your project**. No XML/script is needed, even for the build part !<br/>
This leads in a very lightweight and easy tooling to run/edit/debug builds, coherent with Spring Boot philosophy (just running main methods to get things done).

**Last release:** 2.0.0-SNAPSHOT (compatible with Spring Boot versions : 0.7.0-SNAPSHOT)
 
## Principle

### Writing the build class

Create a build class extending `JkSpringbootBuild` (in _[project Dir]/build/def_ ) as above :

```java
import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkJavaDepScopes;
import org.jerkar.plugins.springboot.JkPluginSpringboot;
import org.jerkar.tool.JkImport;
import org.jerkar.tool.JkInit;
import org.jerkar.tool.JkRun;
import org.jerkar.tool.builtins.java.JkPluginJava;

import static org.jerkar.plugins.springboot.JkSpringModules.Boot;

@JkImport("org.jerkar.plugins:springboot:2.0.0-SNAPSHOT")
@JkImportRepo("https://oss.sonatype.org/content/repositories/snapshots")
class Build extends JkRun {

    private final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    private final JkPluginSpringboot springbootPlugin = getPlugin(JkPluginSpringboot.class); // Load springboot plugin.

    @Override
    protected void setup() {
        springbootPlugin.springbootVersion = "2.0.3.RELEASE";
        javaPlugin.getProject().addDependencies(JkDependencySet.of()
                .and(Boot.STARTER_WEB)
                .and(Boot.STARTER_TEST, JkJavaDepScopes.TEST)
        );
    }

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).javaPlugin.clean().pack();
    }

}
```

Running this class performs :

* Compilation and tests run
* Generation of the original binary jar along its sources jar
* Generation of the executable jar

### Adding extra dependencies
 
Springboot addin provides class constants to declare most of dependencies. 
It adds great comfort when picking some Spring dependencies.
 
```java
    ...
    javaPlugin.getProject().addDependencies(JkDependencySet.of()
            .and(Boot.STARTER_WEB)
            .and(Boot.STARTER_TEST, JkJavaDepScopes.TEST)
            .and(Fwk.JDBC)
            .and(Data.MONGODB)
            .and(Data.COMMONS)
            .and(securityOn, Boot.STARTER_SECURITY)    		  
    );    
}
```

