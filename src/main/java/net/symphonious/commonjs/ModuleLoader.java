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

import java.io.IOException;

/**
 * A ModuleLoader is used to map between a module ID and the actual JavaScript source for that module.
 */
@FunctionalInterface
public interface ModuleLoader
{
    /**
     * Load the source code for the module {@literal moduleId}.  {@literal moduleId} is always a top-level CommonJS module ID (i.e. it never starts with ./ or ../).
     *
     * @param moduleId the top-level CommonJS module ID for the module to load.
     * @return the JavaScript source for the requested module or {@literal null} if it does not exist.
     * @throws IOException if an error occurs while loading a module.
     */
    String loadModule(String moduleId) throws IOException;
}
