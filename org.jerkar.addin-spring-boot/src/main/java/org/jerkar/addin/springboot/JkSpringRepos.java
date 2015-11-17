package org.jerkar.addin.springboot;

import org.jerkar.api.depmanagement.JkRepo;
import org.jerkar.api.depmanagement.JkRepos;

/**
 * Download repositories of <i>Spring IO</i> company. 
 */
public final class JkSpringRepos {
    
    public static final JkRepo SNAPSHOT = JkRepo.maven("https://repo.spring.io/snapshot/");

    public static final JkRepo MILESTONE = JkRepo.maven("https://repo.spring.io/milestone/");
    
    public static final JkRepo RELEASE = JkRepo.maven("https://repo.spring.io/release/");
    
    public static final JkRepos ALL = RELEASE.and(MILESTONE).and(SNAPSHOT);
    
}
