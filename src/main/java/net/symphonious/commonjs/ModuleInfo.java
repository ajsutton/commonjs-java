package net.symphonious.commonjs;

final class ModuleInfo
{
    private final String moduleId;
    private final String source;

    ModuleInfo(final String moduleId, final String source)
    {
        this.moduleId = moduleId;
        this.source = source;
    }

    public String getModuleId()
    {
        return moduleId;
    }

    public String getSource()
    {
        return source;
    }
}
