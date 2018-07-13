package org.jerkar.plugins.springboot;

import org.jerkar.api.depmanagement.JkRepo;
import org.jerkar.api.depmanagement.JkRepoSet;

/**
 * Download repositories of <i>Spring IO</i> company.
 */
public final class JkSpringRepos {

    public static final JkRepo SNAPSHOT = JkRepo.of("https://repo.spring.io/snapshot/");

    public static final JkRepo MILESTONE = JkRepo.of("https://repo.spring.io/milestone/");

    public static final JkRepo RELEASE = JkRepo.of("https://repo.spring.io/release/");

    public static final JkRepoSet ALL = JkRepoSet.of(RELEASE, MILESTONE, SNAPSHOT);

}
