// Copyright 2011 The MOE Authors All Rights Reserved.

package com.google.devtools.moe.client.tools;

import static org.easymock.EasyMock.expect;

import com.google.common.collect.ImmutableList;
import com.google.devtools.moe.client.CommandRunner;
import com.google.devtools.moe.client.FileSystem;
import com.google.devtools.moe.client.Injector;
import com.google.devtools.moe.client.testing.TestingModule;
import com.google.devtools.moe.client.tools.FileDifference.Comparison;

import dagger.Provides;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.io.File;

import javax.inject.Singleton;

/**
 * @author dbentley@google.com (Daniel Bentley)
 */
public class FileDifferenceTest extends TestCase {

  private final IMocksControl control = EasyMock.createControl();
  private final FileSystem fileSystem = control.createMock(FileSystem.class);
  private final CommandRunner cmd = control.createMock(CommandRunner.class);

  // TODO(cgruber): Rework these when statics aren't inherent in the design.
  @dagger.Component(modules = {TestingModule.class, Module.class})
  @Singleton
  interface Component {
    Injector context(); // TODO (b/19676630) Remove when bug is fixed.
  }

  @dagger.Module
  class Module {
    @Provides
    public CommandRunner commandRunner() {
      return cmd;
    }

    @Provides
    public FileSystem fileSystem() {
      return fileSystem;
    }
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    Injector.INSTANCE =
        DaggerFileDifferenceTest_Component.builder().module(new Module()).build().context();
  }

  public void testExistence() throws Exception {
    File file1 = new File("/1/foo");
    File file2 = new File("/2/foo");

    expect(fileSystem.exists(file1)).andReturn(true);
    expect(fileSystem.exists(file2)).andReturn(false);
    expect(fileSystem.isExecutable(file1)).andReturn(false);
    expect(fileSystem.isExecutable(file2)).andReturn(false);
    expect(cmd.runCommand("diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), ""))
        .andThrow(
            new CommandRunner.CommandException(
                "diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), "foo", "", 1));

    control.replay();
    FileDifference d = FileDifference.CONCRETE_FILE_DIFFER.diffFiles("foo", file1, file2);
    control.verify();
    assertEquals(Comparison.ONLY1, d.existence);
    assertEquals(Comparison.SAME, d.executability);
    assertEquals("foo", d.contentDiff);
  }

  public void testExistence2() throws Exception {
    File file1 = new File("/1/foo");
    File file2 = new File("/2/foo");

    expect(fileSystem.exists(file1)).andReturn(false);
    expect(fileSystem.exists(file2)).andReturn(true);
    expect(fileSystem.isExecutable(file1)).andReturn(false);
    expect(fileSystem.isExecutable(file2)).andReturn(false);
    expect(cmd.runCommand("diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), ""))
        .andThrow(
            new CommandRunner.CommandException(
                "diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), "foo", "", 1));

    control.replay();
    FileDifference d = FileDifference.CONCRETE_FILE_DIFFER.diffFiles("foo", file1, file2);
    control.verify();
    assertEquals(Comparison.ONLY2, d.existence);
    assertEquals(Comparison.SAME, d.executability);
    assertEquals("foo", d.contentDiff);
  }

  public void testExecutability() throws Exception {
    File file1 = new File("/1/foo");
    File file2 = new File("/2/foo");

    expect(fileSystem.exists(file1)).andReturn(true);
    expect(fileSystem.exists(file2)).andReturn(true);
    expect(fileSystem.isExecutable(file1)).andReturn(true);
    expect(fileSystem.isExecutable(file2)).andReturn(false);
    expect(cmd.runCommand("diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), "")).andReturn("");

    control.replay();
    FileDifference d = FileDifference.CONCRETE_FILE_DIFFER.diffFiles("foo", file1, file2);
    control.verify();
    assertEquals(Comparison.SAME, d.existence);
    assertEquals(Comparison.ONLY1, d.executability);
    assertEquals(null, d.contentDiff);
  }

  public void testExecutability2() throws Exception {
    File file1 = new File("/1/foo");
    File file2 = new File("/2/foo");

    expect(fileSystem.exists(file1)).andReturn(true);
    expect(fileSystem.exists(file2)).andReturn(true);
    expect(fileSystem.isExecutable(file1)).andReturn(false);
    expect(fileSystem.isExecutable(file2)).andReturn(true);
    expect(cmd.runCommand("diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), "")).andReturn("");

    control.replay();
    FileDifference d = FileDifference.CONCRETE_FILE_DIFFER.diffFiles("foo", file1, file2);
    control.verify();
    assertEquals(Comparison.SAME, d.existence);
    assertEquals(Comparison.ONLY2, d.executability);
    assertEquals(null, d.contentDiff);
  }

  public void testContents() throws Exception {
    File file1 = new File("/1/foo");
    File file2 = new File("/2/foo");

    expect(fileSystem.exists(file1)).andReturn(true);
    expect(fileSystem.exists(file2)).andReturn(true);
    expect(fileSystem.isExecutable(file1)).andReturn(true);
    expect(fileSystem.isExecutable(file2)).andReturn(true);
    expect(cmd.runCommand("diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), ""))
        .andThrow(
            new CommandRunner.CommandException(
                "diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), "foo", "", 1));

    control.replay();
    FileDifference d = FileDifference.CONCRETE_FILE_DIFFER.diffFiles("foo", file1, file2);
    control.verify();
    assertEquals(Comparison.SAME, d.existence);
    assertEquals(Comparison.SAME, d.executability);
    assertEquals("foo", d.contentDiff);
  }

  public void testExecutabilityAndContents() throws Exception {
    File file1 = new File("/1/foo");
    File file2 = new File("/2/foo");

    expect(fileSystem.exists(file1)).andReturn(true);
    expect(fileSystem.exists(file2)).andReturn(true);
    expect(fileSystem.isExecutable(file1)).andReturn(true);
    expect(fileSystem.isExecutable(file2)).andReturn(false);
    expect(cmd.runCommand("diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), ""))
        .andThrow(
            new CommandRunner.CommandException(
                "diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), "foo", "", 1));

    control.replay();
    FileDifference d = FileDifference.CONCRETE_FILE_DIFFER.diffFiles("foo", file1, file2);
    control.verify();
    assertEquals(Comparison.SAME, d.existence);
    assertEquals(Comparison.ONLY1, d.executability);
    assertEquals("foo", d.contentDiff);
  }

  public void testIdentical() throws Exception {
    File file1 = new File("/1/foo");
    File file2 = new File("/2/foo");

    expect(fileSystem.exists(file1)).andReturn(true);
    expect(fileSystem.exists(file2)).andReturn(true);
    expect(fileSystem.isExecutable(file1)).andReturn(false);
    expect(fileSystem.isExecutable(file2)).andReturn(false);
    expect(cmd.runCommand("diff", ImmutableList.of("-N", "/1/foo", "/2/foo"), "")).andReturn("");

    control.replay();
    FileDifference d = FileDifference.CONCRETE_FILE_DIFFER.diffFiles("foo", file1, file2);
    control.verify();
    assertFalse(d.isDifferent());
  }
}
