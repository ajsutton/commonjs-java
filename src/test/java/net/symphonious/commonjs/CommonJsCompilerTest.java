package net.symphonious.commonjs;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CommonJsCompilerTest
{
    private static final String SIMPLE_JAVASCRIPT = "'Hello world!';";

    private final InMemoryModuleLoader moduleLoader = new InMemoryModuleLoader();
    private final CommonJsCompiler compiler = new CommonJsCompiler(moduleLoader);

    @Test
    public void shouldCompileFileWithNoDependencies() throws Exception
    {
        moduleLoader.addModule("main", SIMPLE_JAVASCRIPT);

        assertScriptProducesOutput(compiler.compile("main"), "Hello world!");
    }

    private void assertScriptProducesOutput(final String script, final String expectedOutput) throws ScriptException
    {
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        final ScriptEngine javascriptEngine = scriptEngineManager.getEngineByMimeType("text/javascript");
        assertThat(javascriptEngine.eval(script), is(expectedOutput));
    }
}