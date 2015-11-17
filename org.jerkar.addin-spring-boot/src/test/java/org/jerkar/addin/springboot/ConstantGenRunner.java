package org.jerkar.addin.springboot;

import java.util.List;

import org.jerkar.api.depmanagement.JkModuleId;
import org.jerkar.api.utils.JkUtilsIO;
import org.jerkar.api.utils.JkUtilsString;

class ConstantGenRunner {
    
    public static void main(String[] args) {
	//String version = "orgSpringframeworkBootVersion";
	//String prefix = "SPRING-BOOT-";
	
	//String version = "orgSpringframeworkVersion";
	//String prefix = "SPRING-";
	
	//String version = "orgSpringframeworkIntegrationVersion";
	//String prefix = "SPRING-INTEGRATION-";
	
	// String version = "orgSpringframeworkSecurityVersion";
	// String prefix = "SPRING-SECURITY-";
	
	// String version = "data";
	// String prefix = "SPRING-DATA-";
	
	// String version = "social";
	// String prefix = "SPRING-SOCIAL-";
	
	String version = "cloud";
	String prefix = "SPRING-CLOUD-";
	
	List<String> lines = JkUtilsIO.readAsLines(ConstantGenRunner.class.getResourceAsStream("modules-" + version + ".txt"));
	for (String line : lines) {
	    String afterAnd = JkUtilsString.substringAfterFirst(line,".and(\"");
	    String groupAndName = JkUtilsString.substringBeforeFirst(afterAnd, "\",");
	    JkModuleId moduleId = JkModuleId.of(groupAndName);
	    String name = moduleId.name().toUpperCase();
	    name = JkUtilsString.substringAfterFirst(name, prefix);
	    name = name.replace('-','_');
	    System.out.println("public static final JkModuleId " + name + " = module(\""+moduleId.name()+"\");");
	    System.out.println();
	}
	System.out.println("--------------------------------------------------------");
	
	for (String line : lines) {
	    String afterAnd = JkUtilsString.substringAfterFirst(line,".and(\"");
	    String groupAndName = JkUtilsString.substringBeforeFirst(afterAnd, "\",");
	    JkModuleId moduleId = JkModuleId.of(groupAndName);
	    String name = moduleId.name().toUpperCase();
	    name = JkUtilsString.substringAfterFirst(name, prefix);
	    name = name.replace('-','_');
	    System.out.println("                .and(" + name + ", "+version +")");
	}
	
    }

}
