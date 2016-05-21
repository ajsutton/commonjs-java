package net.symphonious.commonjs;

public class BundleInfo
{
    private final String mainModuleId;
    private final ModuleInfo[] modules;

    public BundleInfo(final String mainModuleId, final ModuleInfo... modules)
    {
        this.mainModuleId = mainModuleId;
        this.modules = modules;
    }

    public String getMainModuleId()
    {
        return mainModuleId;
    }

    public ModuleInfo[] getModules()
    {
        return modules;
    }
}
