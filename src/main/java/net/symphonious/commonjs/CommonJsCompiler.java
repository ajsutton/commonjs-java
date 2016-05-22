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
import java.util.stream.Stream;

/**
 * A CommonJS compiler - given a set of module IDs, it will combine them and all their dependencies into a single JavaScript file that can be shipped to the browser.
 *
 * <p>An implementation of {@link ModuleLoader} must be provided that when given an module ID, returns the source code for that module or <code>null</code> if the module does not exist.</p>
 */
public class CommonJsCompiler
{
    private final ModuleLoader moduleLoader;
    private DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
    private Mustache javascriptTemplate = mustacheFactory.compile("bundle.js.mustache");

    /**
     * Create a new CommonJsCompiler with the supplied {@link ModuleLoader}.
     *
     * @param moduleLoader the implementation of {@link ModuleLoader} to use to load source code for the modules.
     */
    public CommonJsCompiler(final ModuleLoader moduleLoader)
    {
        this.moduleLoader = moduleLoader;
    }

    /**
     * Load a set of modules, combined with their dependencies (transitively) and combine them all into a single JavaScript file.
     *
     * @param moduleIds the module IDs for the modules to load.
     * @return a String of JavaScript that combines the requested modules plus their transitive dependencies.
     */
    public String compile(final String... moduleIds)
    {
        final ModuleSet modules = new ModuleSet(moduleLoader);
        Stream.of(moduleIds).forEach(modules::addModule);

        final Writer out = new StringWriter();
        javascriptTemplate.execute(out, new BundleInfo(moduleIds, modules.getModules()));
        return out.toString();
    }
}
