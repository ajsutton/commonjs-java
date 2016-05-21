package net.symphonious.commonjs;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CommonJsCompilerTest
{
    private static final String SIMPLE_JAVASCRIPT = "console.log('Hello World!');";

    private final InMemoryModuleLoader moduleLoader = new InMemoryModuleLoader();
    private final CommonJsCompiler compiler = new CommonJsCompiler(moduleLoader);

    @Test
    public void shouldCompileFileWithNoDependencies() throws Exception
    {
        moduleLoader.addModule("main", SIMPLE_JAVASCRIPT);

        assertThat(compiler.compile("main"), is(SIMPLE_JAVASCRIPT));
    }
}