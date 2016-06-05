package net.symphonious.commonjs;

import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DependencyFinderTest
{
    private final DependencyFinder dependencyFinder = new DependencyFinder();

    @Test
    public void shouldHaveNoDependenciesWhenRequireIsNotCalled() throws Exception
    {
        assertDependencies("console.log('Hello world!');");
    }

    @Test
    public void shouldDeduplicateDependencies() throws Exception
    {
        assertDependencies("require('dep1'); require('dep1');", "dep1");
    }

    @Test
    public void shouldNotAddDependencyWhenUserCallsRequireWithNoArguments() throws Exception
    {
        assertDependencies("require();");
    }

    @Test
    public void shouldNotAddDependencyWhenUserCallRequireWithMoreThanOneArgument() throws Exception
    {
        assertDependencies("require('dep1', 'dep2');");
    }

    @Test
    public void shouldNotTreatCallsToLocalVariableCalledRequireADependency() throws Exception
    {
        assertDependencies("var require = function() {}; var x = require('dep1');");
    }

    @Test
    public void shouldBeAbleToLoadDependenciesUsingRelativePathFromRootModule() throws Exception
    {
        assertDependenciesFrom("main", "require('./dep1');", "dep1");
    }

    @Test
    public void shouldBeAbleToLoadDependenciesUsingRelativePathFromModuleInSubFolder() throws Exception
    {
        assertDependenciesFrom("sub/main", "require('./dep1');", "sub/dep1");
    }

    @Test
    public void shouldBeAbleToLoadDependenciesFromParentDirectoriesUsingRelativePath() throws Exception
    {
        assertDependenciesFrom("sub1/sub2/sub3/main", "require('../../dep1');", "sub1/dep1");
    }

    @Test
    public void shouldNotGoPastTopLevelDirectoryWhenReferringToParentDirectories() throws Exception
    {
        assertDependenciesFrom("sub/main", "require('../../../../dep1');", "dep1");
    }

    private void assertDependencies(final String script, final String... expectedDependencies)
    {
        assertDependenciesFrom("main", script, expectedDependencies);
    }

    private void assertDependenciesFrom(final String currentModulePath, final String script, final String... expectedDependencies)
    {
        assertThat(dependencyFinder.findDependencies(new ModuleInfo(currentModulePath, script)), is(new HashSet<>(asList(expectedDependencies))));
    }
}