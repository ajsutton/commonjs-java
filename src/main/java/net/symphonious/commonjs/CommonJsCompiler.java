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

import net.symphonious.commonjs.sourcemap.SourceMapBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A CommonJS compiler - given a set of module IDs, it will combine them and all their dependencies into a single JavaScript file that can be shipped to the browser.
 * <p>An implementation of {@link ModuleLoader} must be provided that when given an module ID, returns the source code for that module or <code>null</code> if the module does not exist.</p>
 */
public class CommonJsCompiler
{
    private final ModuleLoader moduleLoader;

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
     * @return a CompilationResult containing a String of JavaScript that combines the requested modules plus their transitive dependencies along with the sourcemap for the JavaScript.
     * @throws IOException if an error occurs while loading a module.
     */
    public CompilationResult compile(final String... moduleIds) throws IOException
    {
        final StringWriter out = new StringWriter();
        final SourceMapBuilder sourceMapBuilder = new SourceMapBuilder();
        final ModuleSet modules = new ModuleSet(moduleLoader);
        for (final String moduleId : moduleIds)
        {
            modules.addModule(moduleId);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(CommonJsCompiler.class.getResourceAsStream("machinery.js"), StandardCharsets.UTF_8)))
        {
            for (int c = in.read(); c >= 0; c = in.read())
            {
                if (c == '\n')
                {
                    sourceMapBuilder.skipLine();
                }
                out.append((char)c);
            }
        }

        out.append("(");
        out.append(Stream.of(modules.getModules())
                   .map(moduleInfo -> {
                       sourceMapBuilder.skipLine();
                       sourceMapBuilder.appendModule(moduleInfo.getModuleId(), moduleInfo.getSource());
                       return "'" + moduleInfo.getModuleId() + "': function(module, exports, require) {\n" +
                              moduleInfo.getSource() +
                              "\n}";
                   })
                   .collect(Collectors.joining(",", "{", "}")));
        out.append(", ");
        out.append(Stream.of(moduleIds)
                   .map(id -> "'" + id + "'")
                   .collect(Collectors.joining(",", "[", "]")));
        out.append(");\n");
        return new CompilationResult(out.toString(), sourceMapBuilder.generateSourceMap());
    }
}
