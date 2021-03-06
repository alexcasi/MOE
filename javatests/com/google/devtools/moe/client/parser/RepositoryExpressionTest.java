// Copyright 2011 The MOE Authors All Rights Reserved.

package com.google.devtools.moe.client.parser;

import com.google.common.collect.ImmutableMap;
import com.google.devtools.moe.client.MoeProblem;
import com.google.devtools.moe.client.project.ProjectContext;
import com.google.devtools.moe.client.repositories.Repository;
import com.google.devtools.moe.client.testing.DummyRepositoryFactory;

import junit.framework.TestCase;

/**
 */
public class RepositoryExpressionTest extends TestCase {

  public void testMakeWriter_NonexistentRepository() throws Exception {
    try {
      new RepositoryExpression("internal").createWriter(ProjectContext.builder().build());
      fail();
    } catch (MoeProblem expected) {
      assertEquals("No such repository 'internal' in the config. Found: []", expected.getMessage());
    }
  }

  public void testMakeWriter_DummyRepository() throws Exception {
    Repository.Factory repositoryFactory = new DummyRepositoryFactory();
    ProjectContext context =
        ProjectContext.builder()
            .withRepositories(
                ImmutableMap.of("internal", repositoryFactory.create("internal", null)))
            .build();
    new RepositoryExpression("internal").createWriter(context);
  }
}
