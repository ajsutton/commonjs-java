package net.symphonious.commonjs;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Minifier
{
    public CompilationResult minify(final CompilationResult compilationResult) throws IOException
    {
        final ScriptEngine javascriptEngine = prepareScriptEngine();
        javascriptEngine.put("script", compilationResult.getJavaScript());
        javascriptEngine.put("sourceMap", compilationResult.getSourceMap());
        try
        {
            javascriptEngine.eval("var result = compressScript(script, sourceMap);");
            return new CompilationResult((String)javascriptEngine.eval("result.script"),
                                         (String)javascriptEngine.eval("result.sourceMap"));
        }
        catch (ScriptException e)
        {
            throw new RuntimeException("Failed to compress script", e);
        }
    }

    private ScriptEngine prepareScriptEngine()
    {
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        final ScriptEngine javascriptEngine = scriptEngineManager.getEngineByMimeType("text/javascript");
        loadLibrary(javascriptEngine, "source-map.js");
        loadLibrary(javascriptEngine, "uglify2.js");
        loadLibrary(javascriptEngine, "compressor.js");
        return javascriptEngine;
    }

    private void loadLibrary(final ScriptEngine javascriptEngine, final String libraryFileName)
    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(CommonJsCompiler.class.getResourceAsStream(libraryFileName), StandardCharsets.UTF_8)))
        {
            javascriptEngine.eval(in);
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Failed to load uglify2.js", e);
        }
    }
}
