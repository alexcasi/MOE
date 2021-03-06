// Copyright 2011 The MOE Authors All Rights Reserved.

package com.google.devtools.moe.client.logic;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.devtools.moe.client.Injector;
import com.google.devtools.moe.client.database.Db;
import com.google.devtools.moe.client.database.RepositoryEquivalence;
import com.google.devtools.moe.client.database.RepositoryEquivalenceMatcher;
import com.google.devtools.moe.client.database.RepositoryEquivalenceMatcher.Result;
import com.google.devtools.moe.client.migrations.Migration;
import com.google.devtools.moe.client.migrations.MigrationConfig;
import com.google.devtools.moe.client.project.ProjectContext;
import com.google.devtools.moe.client.repositories.Repository;
import com.google.devtools.moe.client.repositories.Revision;
import com.google.devtools.moe.client.repositories.RevisionHistory.SearchType;

import java.util.List;

/**
 * Given a {@link MigrationConfig} from Repo A to Repo B, return a List of {@link Migration}s
 * encapsulating all the changes from A that haven't been ported to B yet since their last
 * {@link RepositoryEquivalence}.
 *
 */
public class DetermineMigrationsLogic {

  /**
   * @param migrationConfig  the migration specification
   * @param db  the MOE db which will be used to find the last equivalence
   * @return a list of pending Migrations since last {@link RepositoryEquivalence} per
   *     migrationConfig
   */
  public static List<Migration> determineMigrations(
      ProjectContext context, MigrationConfig migrationConfig, Db db) {

    Repository fromRepo = context.getRepository(migrationConfig.getFromRepository());
    // TODO(user): Decide whether to migrate linear or graph history here. Once DVCS Writers
    // support writing a graph of Revisions, we'll need to opt for linear or graph history based
    // on the MigrationConfig (e.g. whether or not the destination repo is linear-only).
    Result equivMatch =
        fromRepo
            .revisionHistory()
            .findRevisions(
                null, // Start at head.
                new RepositoryEquivalenceMatcher(migrationConfig.getToRepository(), db),
                SearchType.LINEAR);

    List<Revision> revisionsSinceEquivalence =
        Lists.reverse(equivMatch.getRevisionsSinceEquivalence().getBreadthFirstHistory());

    if (revisionsSinceEquivalence.isEmpty()) {
      Injector.INSTANCE
          .ui()
          .info(
              "No revisions found since last equivalence for migration '"
                  + migrationConfig.getName()
                  + "'");
      return ImmutableList.of();
    }

    // TODO(user): Figure out how to report all equivalences.
    RepositoryEquivalence lastEq = equivMatch.getEquivalences().get(0);
    Injector.INSTANCE
        .ui()
        .info(
            "Found %d revisions in %s since equivalence (%s): %s",
            revisionsSinceEquivalence.size(),
            migrationConfig.getFromRepository(),
            lastEq,
            Joiner.on(", ").join(revisionsSinceEquivalence));

    if (migrationConfig.getSeparateRevisions()) {
      ImmutableList.Builder<Migration> migrations = ImmutableList.builder();
      for (Revision fromRev : revisionsSinceEquivalence) {
        migrations.add(new Migration(migrationConfig, ImmutableList.of(fromRev), lastEq));
      }
      return migrations.build();
    } else {
      return ImmutableList.of(new Migration(migrationConfig, revisionsSinceEquivalence, lastEq));
    }
  }
}
