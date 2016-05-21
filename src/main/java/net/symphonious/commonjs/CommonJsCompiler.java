package net.symphonious.commonjs;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import java.io.StringWriter;
import java.io.Writer;

public class CommonJsCompiler
{
    private final ModuleLoader moduleLoader;
    private DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();

    public CommonJsCompiler(final ModuleLoader moduleLoader)
    {
        this.moduleLoader = moduleLoader;
    }

    public String compile(final String moduleId)
    {
        final Mustache moduleTemplate = mustacheFactory.compile("module.mustache");
        final Writer out = new StringWriter();
        final String source = moduleLoader.loadModule(moduleId);
        moduleTemplate.execute(out, new ModuleInfo(moduleId, source));
        return out.toString();
    }
}
