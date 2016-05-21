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

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CommonJsCompilerTest
{

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

    private void assertScriptProduces(final String script, final Object expectedOutput, final String... dependencies) throws ScriptException
    {
        moduleLoader.addModule("main", "exports.result = " + script);
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        final ScriptEngine javascriptEngine = scriptEngineManager.getEngineByMimeType("text/javascript");
        final String compiledScript = compiler.compile(Stream.concat(Stream.of("main"), Stream.of(dependencies)).toArray(String[]::new));
        assertThat(javascriptEngine.eval(compiledScript + "require('main').result;"), is(expectedOutput));
    }

}