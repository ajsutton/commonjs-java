package net.symphonious.commonjs;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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

    private void assertScriptProduces(final String script, final Object expectedOutput, final String... dependencies) throws ScriptException
    {
        moduleLoader.addModule("main", "exports.result = " + script);
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        final ScriptEngine javascriptEngine = scriptEngineManager.getEngineByMimeType("text/javascript");
        final String compiledScript = compiler.compile("main", dependencies);
        assertThat(javascriptEngine.eval(compiledScript + "require('main').result;"), is(expectedOutput));
    }

}