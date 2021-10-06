![Build Status](https://github.com/jerkar/springboot-plugin/actions/workflows/push-master.yml/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/dev.jeka/springboot-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22dev.jeka22%20AND%20a:%22springboot-plugin%22)

# Springboot plugin for Jeka

[Jeka](https://jeka.dev) plugin to build Spring Boot applications with minimal effort. <br/>

Caution : use it with Jeka 0.8.14 or later !
 
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

@JkImport("dev.jeka:springboot-plugin:2.0.1.RELEASE")
class Build extends JkCommandSet {

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

Running the main method or executing `jeka java#pack` performs :

* Compilation and tests run
* Generation of the original binary jar along its sources jar
* Generation of the executable jar

Pratically, this plugin reads the Springboot pom/bom for the specified version and enrich the java plugin with dependency version provider according the pom. It also instructs java plugin to produce a workable springboot jar instead of the vanilla jar. 

Utility methods are provided if you want to construct your own springboot jar and dependency version provider without embracing the plugin mechanism.

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

### Scaffolding Springboot tailored build class

* Execute `jeka scaffold#run #springboot`

### How to build this project ?

This repository contains 2 projects. They use Jeka wrapper so you don't need to have Jeka installed on your machine. 
* Execute `jekaw java#pack` from _dev.jeka.plugins.spring-boot_ folder to build the main project. 
* Execute `../dev.jeka.plugins.spring-boot/jekaw java#pack` from _dev.jeka.plugins.spring-boot-sample_ folder to build the sample project.
