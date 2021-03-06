// Copyright 2015 The MOE Authors All Rights Reserved.
package com.google.devtools.moe.client.options;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Performs options/flags parsing.
 */
@Singleton
public class OptionsParser {
  private static final Logger logger = Logger.getLogger(OptionsParser.class.getName());

  private final String[] preprocessedArgs;

  /**
   * Creates the OptionsParser
   *
   * @param preprocessedArgs  An array of command-line arguments expected to have had the
   *     initial command/directive removed.
   * */
  @Inject
  OptionsParser(String[] preprocessedArgs) {
    this.preprocessedArgs = preprocessedArgs;
  }

  /**
   * Parses command-line flags, returning true if the parse was successful and no flags errors were
   * found.
   */
  // TODO(cgruber) Rework this with JCommander so we can have plugin-specific flags
  public boolean parseFlags(MoeOptions options) {
    CmdLineParser parser = new CmdLineParser(options);
    try {
      parser.parseArgument(preprocessedArgs);
      if (options.shouldDisplayHelp()) {
        parser.printUsage(System.err);
      }
      return true;
    } catch (CmdLineException e) {
      logger.log(Level.SEVERE, "Failure", e);
      parser.printUsage(System.err);
      return false;
    }
  }
}
