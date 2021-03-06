// Copyright 2011 The MOE Authors All Rights Reserved.

package com.google.devtools.moe.client.logic;

import com.google.devtools.moe.client.Injector;
import com.google.devtools.moe.client.codebase.Codebase;
import com.google.devtools.moe.client.tools.CodebaseDifference;
import com.google.devtools.moe.client.tools.PatchCodebaseDifferenceRenderer;

/**
 * Performs the logic of the DiffCodebasesDirective
 *
 */
public class DiffCodebasesLogic {
  private static final PatchCodebaseDifferenceRenderer RENDERER =
      new PatchCodebaseDifferenceRenderer();

  /**
   * Prints the diff or lack thereof of the two codebases.
   *
   * @param c1 the Codebase to diff with c2
   * @param c2 the Codebase to diff with c1
   */
  public static void printDiff(Codebase c1, Codebase c2) {
    CodebaseDifference diff = CodebaseDifference.diffCodebases(c1, c2);
    if (diff.areDifferent()) {
      Injector.INSTANCE
          .ui()
          .info("Codebases \"%s\" and \"%s\" differ:\n%s", c1, c2, RENDERER.render(diff));
    } else {
      Injector.INSTANCE.ui().info("Codebases \"%s\" and \"%s\" are identical", c1, c2);
    }
  }
}
