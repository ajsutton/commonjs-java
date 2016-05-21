package net.symphonious.commonjs;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import java.io.StringWriter;
import java.io.Writer;
import java.util.stream.Stream;

public class CommonJsCompiler
{
    private final ModuleLoader moduleLoader;
    private DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();

    public CommonJsCompiler(final ModuleLoader moduleLoader)
    {
        this.moduleLoader = moduleLoader;
    }

    public String compile(final String moduleId, final String... dependencies)
    {
        final Mustache moduleTemplate = mustacheFactory.compile("module.mustache");
        final Writer out = new StringWriter();

        final ModuleInfo[] modules = Stream.concat(Stream.of(createModuleInfo(moduleId)), Stream.of(dependencies).map(this::createModuleInfo))
                                      .toArray(ModuleInfo[]::new);
        moduleTemplate.execute(out, new BundleInfo(moduleId, modules));
        return out.toString();
    }

    private ModuleInfo createModuleInfo(final String moduleId)
    {
        return new ModuleInfo(moduleId, moduleLoader.loadModule(moduleId));
    }
}
