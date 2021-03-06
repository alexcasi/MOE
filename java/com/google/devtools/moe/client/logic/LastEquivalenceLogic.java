// Copyright 2011 The MOE Authors All Rights Reserved.

package com.google.devtools.moe.client.logic;

import com.google.devtools.moe.client.database.Db;
import com.google.devtools.moe.client.database.RepositoryEquivalence;
import com.google.devtools.moe.client.database.RepositoryEquivalenceMatcher;
import com.google.devtools.moe.client.repositories.Revision;
import com.google.devtools.moe.client.repositories.RevisionHistory;
import com.google.devtools.moe.client.repositories.RevisionHistory.SearchType;

import java.util.List;

/**
 * Performs the logic of the LastEquivalenceDirective.
 *
 */
public class LastEquivalenceLogic {

  /**
   * Find the last equivalence.
   *
   * @param toRepo the name of the Repository to look for an equivalence in
   * @param rev the Revision to begin looking for an equivalence at
   * @param db the database to consult for equivalences
   * @param rh the RevisionHistory for rev's Repository
   * @return the most recent Equivalence or null if there wasn't one
   */
  public static List<RepositoryEquivalence> lastEquivalence(
      String toRepo, Revision rev, Db db, RevisionHistory rh) {
    RepositoryEquivalenceMatcher matcher = new RepositoryEquivalenceMatcher(toRepo, db);
    // TODO(user): Determine whether to do a linear or branched search, probably based on a
    // command-line option or a migration config.
    return rh.findRevisions(rev, matcher, SearchType.BRANCHED).getEquivalences();
  }
}
