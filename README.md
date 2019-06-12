[![Build Status](https://travis-ci.org/jerkar/spring-boot-plugin.svg?branch=master)](https://travis-ci.org/jerkar/spring-boot-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/dev.jeka.plugins/springboot.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22dev.jeka.plugins%22%20AND%20a:%22springboot%22)

# Springboot plugin for Jerkar

[Jeka](https://jeka.dev) plugin to build Spring Boot applications with minimal effort. <br/>
 
## Principle

### Writing the build class

Just declare the plugin in your Jeka command class (in _[project Dir]/jeka/def_ ) as above :

```java
import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.depmanagement.JkJavaDepScopes;
import dev.jeka.core.plugins.springboot.JkPluginSpringboot;
import dev.jeka.core.tool.JkImport;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import static dev.jeka.core.plugins.springboot.JkSpringModules.Boot;

@JkImport("dev.jeka.core.plugins:springboot:2.0.0-SNAPSHOT")
@JkImportRepo("https://oss.sonatype.org/content/repositories/snapshots")
class Build extends JkCommands {

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

