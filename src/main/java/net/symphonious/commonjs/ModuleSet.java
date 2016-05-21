/*
 * Copyright 2016 Adrian Sutton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.symphonious.commonjs;

import java.util.HashMap;
import java.util.Map;

class ModuleSet
{
    private final ModuleLoader moduleLoader;
    private final Map<String, ModuleInfo> modules = new HashMap<>();
    private DependencyFinder dependencyFinder = new DependencyFinder();

    public ModuleSet(final ModuleLoader moduleLoader)
    {
        this.moduleLoader = moduleLoader;
    }

    public void addModule(final String moduleId)
    {
        if (!modules.containsKey(moduleId))
        {
            final ModuleInfo module = createModuleInfo(moduleId);
            modules.put(moduleId, module);
            dependencyFinder.findDependencies(module).forEach(this::addModule);
        }
    }

    public ModuleInfo[] getModules()
    {
        return modules.values().stream().toArray(ModuleInfo[]::new);
    }

    private ModuleInfo createModuleInfo(final String moduleId)
    {
        final String source = moduleLoader.loadModule(moduleId);
        if (source == null)
        {
            throw new IllegalArgumentException("Failed to load module: " + moduleId);
        }
        return new ModuleInfo(moduleId, source);
    }
}
