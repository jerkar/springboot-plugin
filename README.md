[![Build Status](https://travis-ci.org/jerkar/spring-boot-plugin.svg?branch=master)](https://travis-ci.org/jerkar/spring-boot-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/dev.jeka.plugins/springboot.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22dev.jeka.plugins%22%20AND%20a:%22springboot%22)

# Springboot plugin for Jerkar

[Jerkar](http://project.jerkar.org) plugin to build Spring Boot applications with minimal effort. <br/>

**Last release:** 2.0.0-SNAPSHOT (compatible with Spring Boot versions : 1.5.x and 2.x)
 
## Principle

### Writing the build class

Just declare the plugin in your Jerkar run class (in _[project Dir]/jerkar/def_ ) as above :

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
 
Springboot plugin provides class constants to declare most of dependencies. 
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

