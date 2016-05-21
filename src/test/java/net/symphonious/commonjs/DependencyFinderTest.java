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

    private void assertDependencies(final String script, final String... expectedDependencies)
    {
        assertThat(dependencyFinder.findDependencies(new ModuleInfo("main", script)), is(new HashSet<>(asList(expectedDependencies))));
    }
}