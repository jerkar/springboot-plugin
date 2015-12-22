# Jerkar Addin for Spring boot

This provides a [Jerkar](http://project.jerkar.org) addin to build Spring Boot applications with minimal effort. <br/>
It honors the promise of using **Java and only Java all over your project**. No XML/script is needed, even for the build part !<br/>
This leads in a very lightweight and easy tooling to run/edit/debug builds, coherent with Spring Boot philosophy (just running main methods to get things done).

**Last release:** 1.3.1.0 (compatible with Spring Boot verions : 1.3.1, 1.3.0, 1.2.8 and 1.2.9)

## Installation

### Prerequisite

You must have [Jerkar installed](http://jerkar.github.io/documentation/latest/getting_started.html) ... easy !
Please use Jerkar 0.3.2 or later.
 
## Principle

### Writing the build class

Create a build class extending `JkSpringbootBuild` (in _[project Dir]/build/def_ ) as above :

```java
import org.jerkar.addin.springboot.JkSpringModules.Boot;
import org.jerkar.addin.springboot.JkSpringbootBuild;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.tool.JkInit;

@JkImport({ "org.jerkar:addin-spring-boot:1.3.1.+"})
class Build extends JkSpringbootBuild {

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

The `JkSpringbootBuild` super class contains dependency management (version of modules to use & modules to excludes from transitive dependency resolution) along classical methods to build and run the application.

### Writing extra run classes

Writing main method is not mandatory but if you like this way of launching build, you can add other runner classes beside the build class to perform other tasks: 

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

@JkImport({ "org.jerkar:addin-spring-boot:1.3.1.+"})
public class Build extends JkSpringbootBuild {

    boolean securityOn = !"off".equals(System.getenv("springboot.security"));

    @Override
    protected JkDependencies dependencies() {
	     return JkDependencies.builder()
		      .on(Boot.STARTER)
		      .on(Fwk.JDBC)
		      .on(Data.MONGODB)
		      .on(Data.COMMONS)
		      .onIf(securityOn, Boot.STARTER_SECURITY)
		      .on(Mobile.DEVICE)
		      .on(JkPopularModules.GUAVA, "18.0")
		      .on(Boot.STARTER_TEST, TEST).build();
    }
    
}
```

## Choosing Spring Boot Version
Now, a given version of Spring Boot addin can work with different versions of Spring Boot. By default the Spring Boot version is the same than the addin. For example, if the addin version is *1.3.1.1*, then the default Spring Boot version will be *1.3.1.RELEASE*.
Nevertheless, if you want to work with another version of Spring Boot, just override `versionManagement` method.

```java
@JkImport({ "org.jerkar:addin-spring-boot:1.3.1.+"})
public class Build extends JkSpringbootBuild {
    ...
  
    @Override
    protected JkSpringbootVersionManagement versionManagement() {
        return JkSpringbootVersionManagement.v1_3_0();
    }
    ...
}

## Getting started

### Using command line

If you want to set up the entire project in a single command line, execute : 
```
jerkar @org.jerkar:addin-spring-boot:1.3.1.+ -buildClass=JkSpringbootBuild scaffold
``` 

This generates a project skeleton with a basic build class in build/def directory.

**Explanation :**

* `@org.jerkar:addin-spring-boot:1.3.1.+` tells Jerkar to use this addin (This will be downloaded from your download repository, Maven central by default). It fetches the lastest verion of the addin starting with '1.3.1.'.
* `-buildClass=JkSpringbootBuild` tells Jerkar to instantiate an object of this class. This class is located in the above addin.
* `scaffold` tells Jerkar to execute `scaffold` method of the previously instantiated object. The `scaffold` method actually creates project directory structure along a basic build class tailored for Spring boot projects. 

If you are an Eclipse user, you can also generate the .project and .classpath in the same round by executing : 
```
jerkar @org.jerkar:addin-spring-boot:1.3.1.+ -buildClass=JkSpringbootBuild scaffold eclipse#
``` 

The `eclipse#` parameter tells Jerkar to activate plugin for Eclipse. The Jerkar plugin for Eclipse alters the `scaffold` method in order to add .classpath and .project files to the scaffolded project. 

### Using Eclipse plugin for Jerkar (do no confuse with above Jerkar plugin for Eclipse)

The following Eclipse plugin *https://github.com/jerkar/eclipsePlugin4Jerkar* makes it all very easy.

* Create an empty Java project in Eclipse (File -> New -> Java Project)
* Right click on it and select Jerkar -> Scafffold -> Spring-Boot Project

That's it, you are ready to code/build/launch your project.

# Todo

The addin is now workable. Some additional features will be added in next releases :

* Support for war files. 

