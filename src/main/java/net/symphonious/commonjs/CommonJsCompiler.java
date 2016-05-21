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

public class CommonJsCompiler
{
    private final ModuleLoader moduleLoader;
    private DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();

    public CommonJsCompiler(final ModuleLoader moduleLoader)
    {
        this.moduleLoader = moduleLoader;
    }

    public String compile(final String... dependencies)
    {
        final Mustache moduleTemplate = mustacheFactory.compile("module.mustache");
        final Writer out = new StringWriter();

        final ModuleInfo[] modules = Stream.of(dependencies).map(this::createModuleInfo).toArray(ModuleInfo[]::new);
        moduleTemplate.execute(out, new BundleInfo(modules));
        return out.toString();
    }

    private ModuleInfo createModuleInfo(final String moduleId)
    {
        return new ModuleInfo(moduleId, moduleLoader.loadModule(moduleId));
    }
}
