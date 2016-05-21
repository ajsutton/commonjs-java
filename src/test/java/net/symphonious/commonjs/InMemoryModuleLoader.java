package net.symphonious.commonjs;

import java.util.HashMap;
import java.util.Map;

class InMemoryModuleLoader implements ModuleLoader
{
    private final Map<String, String> modules = new HashMap<>();

    public InMemoryModuleLoader addModule(final String moduleId, final String moduleSource)
    {
        modules.put(moduleId, moduleSource);
        return this;
    }

    @Override
    public String loadModule(final String moduleId)
    {
        return modules.get(moduleId);
    }
}
