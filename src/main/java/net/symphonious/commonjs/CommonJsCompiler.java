package net.symphonious.commonjs;

public class CommonJsCompiler
{
    private final ModuleLoader moduleLoader;

    public CommonJsCompiler(final ModuleLoader moduleLoader)
    {
        this.moduleLoader = moduleLoader;
    }

    public String compile(final String moduleId)
    {
        return moduleLoader.loadModule(moduleId);
    }
}
