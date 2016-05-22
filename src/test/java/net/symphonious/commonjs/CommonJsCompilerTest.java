/*
 * Copyright 2016 Adrian Sutton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.symphonious.commonjs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CommonJsCompilerTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final InMemoryModuleLoader moduleLoader = new InMemoryModuleLoader();
    private final CommonJsCompiler compiler = new CommonJsCompiler(moduleLoader);

    @Test
    public void shouldCompileFileWithNoDependencies() throws Exception
    {
        assertScriptProduces("'Hello world!';", "Hello world!");
    }

    @Test
    public void shouldBeAbleToAccessModuleName() throws Exception
    {
        assertScriptProduces("'Hello ' + module.id", "Hello main");
    }

    @Test
    public void shouldHaveEmptyObjectAsModuleExports() throws Exception
    {
        assertScriptProduces("module.exports.constructor === Object && Object.keys(module.exports).length === 0", true);
    }

    @Test
    public void shouldMakeModuleExportsAvailableAsExportsVariable() throws Exception
    {
        assertScriptProduces("module.exports === exports", true);
    }

    @Test
    public void shouldProvideARequireFunction() throws Exception
    {
        assertScriptProduces("typeof require === 'function'", true);
    }

    @Test
    public void shouldLoadRequiredDependencies() throws Exception
    {
        moduleLoader.addModule("dep1", "exports.value = 'Hello world!';");
        assertScriptProduces("require('dep1').value;", "Hello world!", "dep1");
    }

    @Test
    public void shouldDetectDependenciesAutomatically() throws Exception
    {
        moduleLoader.addModule("dep1", "exports.value = 'Hello world!';");
        assertScriptProduces("require('dep1').value;", "Hello world!");
    }

    @Test
    public void shouldDetectTransitiveDependencies() throws Exception
    {
        moduleLoader.addModule("dep1", "exports.value = require('dep2').value;");
        moduleLoader.addModule("dep2", "exports.value = 'Hello world!';");
        assertScriptProduces("require('dep1').value;", "Hello world!");
    }

    @Test
    public void shouldNotLoadTheSameDependencyMultipleTimes() throws Exception
    {
        moduleLoader.addModule("dep1", "exports.value = 'Hello world!'");
        final String compiledScript = compileScript("require('dep1').value || require('dep1');");
        assertThat("Should only have included the contents of dep1 once.\n" + compiledScript,
         compiledScript.indexOf("Hello world!"), is(compiledScript.lastIndexOf("Hello world!")));
    }

    @Test
    public void shouldNotLoadTheSameDependencyMultipleTimesWhenRequiredByDifferentFiles() throws Exception
    {
        moduleLoader.addModule("dep1", "exports.value = require('dep2').value;");
        moduleLoader.addModule("dep2", "exports.value = 'Hello world!'");
        final String compiledScript = compileScript("require('dep1').value || require('dep2');");
        assertThat("Should only have included the contents of dep1 once.\n" + compiledScript,
         compiledScript.indexOf("Hello world!"), is(compiledScript.lastIndexOf("Hello world!")));
    }

    @Test
    public void shouldThrowExceptionWhenRequestedModuleIsNotFound() throws Exception
    {
        thrown.expect(IllegalArgumentException.class);

        compiler.compile("main");
    }

    @Test
    public void shouldThrowExceptionWhenRequiredDependencyIsNotFound() throws Exception
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("dep1"));
        compileScript("require('dep1');");
    }

    @Test
    public void shouldNotDieWhenUserCallsRequireWithNoArguments() throws Exception
    {
        assertScriptProduces("require();", null);
    }

    @Test
    public void shouldResolveRelativeModuleIds() throws Exception
    {
        moduleLoader.addModule("deps/dep1", "exports.value = require('./dep2').value;");
        moduleLoader.addModule("deps/dep2", "exports.value = 'Hello world!';");
        assertScriptProduces("require('deps/dep1').value;", "Hello world!");
    }

    @Test
    public void shouldResolveRelativeModuleIdsUsingParentDirectory() throws Exception
    {
        moduleLoader.addModule("deps/dep1", "exports.value = require('../dep2').value;");
        moduleLoader.addModule("dep2", "exports.value = 'Hello world!';");
        assertScriptProduces("require('deps/dep1').value;", "Hello world!");
    }

    @Test
    public void shouldResolveRelativeModuleIdsUsingParentOfParentDirectory() throws Exception
    {
        moduleLoader.addModule("deep/deep/deps/dep1", "exports.value = require('../../../dep2').value;");
        moduleLoader.addModule("dep2", "exports.value = 'Hello world!';");
        assertScriptProduces("require('deep/deep/deps/dep1').value;", "Hello world!");
    }

    @Test
    public void shouldBeAbleToAssignToModuleExports() throws Exception
    {
        moduleLoader.addModule("dep1", "module.exports = 'Hello world!';");
        assertScriptProduces("require('dep1');", "Hello world!");
    }

    @Test
    public void shouldGetAtLeastThePropertiesAlreadyAddedToExportsWhenThereIsACircularDependency() throws Exception
    {
        moduleLoader.addModule("dep1", "exports.value1 = 'A'; require('dep2'); exports.value2 = 'B';");
        moduleLoader.addModule("dep2", "exports.value = require('dep1').value1;");
        assertScriptProduces("require('dep2').value;", "A");
    }

    private void assertScriptProduces(final String script, final Object expectedOutput, final String... dependencies) throws ScriptException
    {
        final String compiledScript = compileScript("exports.result = " + script, dependencies);
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        final ScriptEngine javascriptEngine = scriptEngineManager.getEngineByMimeType("text/javascript");
        try
        {
            assertThat("Did not get expected result from compiled script:\n" + compiledScript, javascriptEngine.eval(compiledScript + "require('main').result;"), is(expectedOutput));
        }
        catch (final ScriptException e)
        {
            fail("Script generated an error: " + e.getMessage() + "\n" + compiledScript);
        }
    }

    private String compileScript(final String script, final String... dependencies)
    {
        moduleLoader.addModule("main", script);
        return compiler.compile(Stream.concat(Stream.of("main"), Stream.of(dependencies)).toArray(String[]::new));
    }

}