package org.jerkar.addin.springboot;

import java.io.File;

import org.jerkar.tool.JkInit;

public class ScaffoldRunner {
	
	public static void main(String[] args) {
		File file = new File("build/output/scaffolded");
		JkSpringbootBuild build = JkInit.instanceOf(JkSpringbootBuild.class, file,  args);
		build.scaffold();
	}

}
