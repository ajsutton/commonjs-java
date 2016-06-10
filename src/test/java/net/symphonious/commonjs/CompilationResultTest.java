package net.symphonious.commonjs;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CompilationResultTest
{
    private final CompilationResult result = new CompilationResult("javascript", "sourcemap");

    @Test
    public void shouldReturnJavaScriptWithSourceMapUrl() throws Exception
    {
        assertThat(result.getJavaScriptWithSourceMapUrl("test.js.map"), is("javascript\n" +
                                                                           "//# sourceMappingURL=test.js.map"));
    }

    @Test
    public void shouldRetrieveJavaScriptWithEmbeddedSourceMap() throws Exception
    {
        assertThat(result.getJavaScriptWithEmbeddedSourceMap(), is("javascript\n" +
                                                                   "//# sourceMappingURL=data:application/json;base64,c291cmNlbWFw"));
    }
}