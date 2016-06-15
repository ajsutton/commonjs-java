package net.symphonious.commonjs;

import net.symphonious.commonjs.sourcemap.SourceMapBuilder;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MinifierTest
{
    private final Minifier minifier = new Minifier();

    @Test
    public void shouldCompressScript() throws Exception
    {
        final String code = "(function() {\n" +
                                     "var myVariable = 'Hello World;';\n" +
                                     "\n" +
                                     "console.log(myVariable);" +
                                     "})();";

        final SourceMapBuilder sourceMapBuilder = new SourceMapBuilder();
        sourceMapBuilder.appendModule("main", code);
        final CompilationResult result = minifier.minify(new CompilationResult(code, sourceMapBuilder.generateSourceMap()));
        assertThat(result.getJavaScript(), is("!function(){var o=\"Hello World;\";console.log(o)}();"));
        assertThat(result.getSourceMap(), is("{\"version\":3," +
                                              "\"sources\":[\"main.js\"]," +
                                              "\"names\":[\"myVariable\",\"console\",\"log\"]," +
                                              "\"mappings\":\"CAAA,WACA,GAAAA,GAAA,cAEAC,SAAAC,IAAAF\"," +
                                              "\"sourcesContent\":[\"(function() {\\nvar myVariable = 'Hello World;';\\n\\nconsole.log(myVariable);})();\"]}"));
    }
}