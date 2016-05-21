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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class CommonJsCompiler
{
    private final ModuleLoader moduleLoader;
    private DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
    private DependencyFinder dependencyFinder = new DependencyFinder();

    public CommonJsCompiler(final ModuleLoader moduleLoader)
    {
        this.moduleLoader = moduleLoader;
    }

    public String compile(final String... dependencies)
    {
        final Mustache moduleTemplate = mustacheFactory.compile("module.mustache");
        final Writer out = new StringWriter();

        final Function<String, ModuleInfo> moduleInfoBuilder = cachingModuleInfoBuilder();
        final Collection<ModuleInfo> requiredModules = Stream.of(dependencies).map(moduleInfoBuilder).collect(toSet());
        requiredModules.addAll(findDependencies(moduleInfoBuilder, requiredModules));


        final ModuleInfo[] modules = requiredModules.stream().toArray(ModuleInfo[]::new);
        moduleTemplate.execute(out, new BundleInfo(dependencies, modules));
        return out.toString();
    }

    private List<ModuleInfo> findDependencies(final Function<String, ModuleInfo> moduleInfoBuilder, final Collection<ModuleInfo> requiredModules)
    {
        final List<ModuleInfo> additionalModules = requiredModules.stream()
                                          .flatMap(module -> dependencyFinder.findDependencies(module).stream().map(moduleInfoBuilder))
                                          .collect(Collectors.toList());
        if (!additionalModules.isEmpty())
        {
            additionalModules.addAll(findDependencies(moduleInfoBuilder, additionalModules));
        }
        return additionalModules;
    }

    private Function<String, ModuleInfo> cachingModuleInfoBuilder()
    {
        final Map<String, ModuleInfo> cache = new HashMap<>();
        return moduleId -> cache.computeIfAbsent(moduleId, this::createModuleInfo);
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
