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
        assertSingleScriptProduces("'Hello world!';", "Hello world!");
    }

    @Test
    public void shouldBeAbleToAccessModuleName() throws Exception
    {
        assertSingleScriptProduces("'Hello ' + module.id", "Hello main");
    }

    @Test
    public void shouldHaveEmptyObjectAsModuleExports() throws Exception
    {
        assertSingleScriptProduces("module.exports.constructor === Object && Object.keys(module.exports).length === 0", true);
    }

    @Test
    public void shouldMakeModuleExportsAvailableAsExportsVariable() throws Exception
    {
        assertSingleScriptProduces("module.exports === exports", true);
    }

    private void assertSingleScriptProduces(final String script, final Object expectedOutput) throws ScriptException
    {
        moduleLoader.addModule("main", script);
        assertScriptProducesOutput(compiler.compile("main"), expectedOutput);
    }

    private void assertScriptProducesOutput(final String script, final Object expectedOutput) throws ScriptException
    {
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        final ScriptEngine javascriptEngine = scriptEngineManager.getEngineByMimeType("text/javascript");
        assertThat(javascriptEngine.eval(script), is(expectedOutput));
    }
}