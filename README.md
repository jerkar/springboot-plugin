# Jerkar Addin for Spring boot

The goal of this project is to provide a Jerkar addin to build Spring Boot application with minimal effort. <br/>
It honors the promise of using Java and only Java for development. No XML/script is needed, even for the build part !<br/>
This leads in a very lightweight and easy tooling to run/edit/debug builds, coherent with Spring Boot philosophy (just running main methods to get things done).

## Installation

### Prerequisite

You must have [Jerkar installed and IDE setup](http://jerkar.github.io/documentation/latest/getting_started.html).
 
## Principle

### Writing the build class

Create a build class extending `JkSpringbootBuild` (in _[project Dir]/build/def_ ) as above :

```java
import org.jerkar.addin.springboot.JkSpringModules.Boot;
import org.jerkar.addin.springboot.JkSpringbootBuild;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.tool.JkInit;

@JkImport({ "org.jerkar:addin-spring-boot:0.1-SNAPSHOT"})
public class Build extends JkSpringbootBuild {

    @Override
    protected JkDependencies dependencies() {
	     return JkDependencies.builder()
		      .on(Boot.STARTER)
		      .on(Boot.STARTER_TEST, TEST).build();
    }
    
    public static void main(String[] args) {
	     JkInit.instanceOf(Build.class, args).doDefault();
    }
}
```

Running this class performs :

* Compilation and tests run
* Generation of the original binary jar along its sources jar
* Generation of the executable jar
* Generation of the executable war file (if WEB-INF present)


### Writing extra run classes

You can also add other runner classes beside the build class to perform other tasks: 

```java
import org.jerkar.tool.JkInit;

class RunApplication {

    public static void main(String[] args) {
        Build build = JkInit.instanceOf(Build.class, args);
        build.doCompile();
        build.run();
    }

}
```

This class compiles code an run the application upon the compiled code and declared dependencies.
 
## Adding extra dependencies
 
Springboot addin provides class constants to declare most of dependencies. 
It adds great comfort when picking some Spring dependencies.
 
 
```java
import org.jerkar.addin.springboot.JkSpringbootBuild;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.tool.JkInit;

import static org.jerkar.addin.springboot.JkSpringModules.*;

@JkImport({ "org.jerkar:addin-spring-boot:0.1-SNAPSHOT"})
public class Build extends JkSpringbootBuild {

    @Override
    protected JkDependencies dependencies() {
	     return JkDependencies.builder()
		      .on(Boot.STARTER)
		      .on(Fwk.JDBC)
		      .on(Data.MONGODB)
		      .on(Data.COMMONS)
		      .on(Security.CORE)
		      .on(Mobile.DEVICE)
		      .on(JkPopularModules.GUAVA, "18.0")
		      .on(Boot.STARTER_TEST, TEST).build();
    }
    
    public static void main(String[] args) {
	     JkInit.instanceOf(Build.class, args).doDefault();
    }
}
```
## Project scaffolding

If you don't want to set up the entire project in a single command line, execute : `jerkar @org.jerkar:addin-spring-boot:1.2.7.0-SNAPSHOT -buildClass=JkSpringbootBuild scaffold`.

Explanation : 

* `@org.jerkar:addin-spring-boot:1.2.7.0-SNAPSHOT` tells jerkar tu use this Jerkar addin (This addin will be uploaded from you download repository).
* `-buildClass=JkSpringbootBuild` tells Jerkar to instanciate an object of this class.
* `scaffold` tells Jerkar to execute `scaffold` method of the previously instantiated object. Th scaffold method actually create project directory structure along a basic build class tailored for Spring boot project. 

If you are an Eclipse user, you can also generate the .project and .classpath in the same round by executing : `jerkar @org.jerkar:addin-spring-boot:1.2.7.0-SNAPSHOT -buildClass=JkSpringbootBuild scaffold eclipse#`. 

The `eclipse#` parameter tells Jerkar to activate Eclipse plugin. The Ecliple plugin alter the scaffold method in order to add .classpath and .project file creation to the scaffolded project. 
