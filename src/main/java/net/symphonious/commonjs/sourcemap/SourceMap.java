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
package net.symphonious.commonjs.sourcemap;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection", "FieldCanBeLocal"})
public class SourceMap
{
    private final int version = 3;
    private final List<String> sources;
    private final List<String> sourcesContent;
    private final List<String> names = new ArrayList<>();
    private final String mappings;

    public SourceMap(final List<String> sources, final List<String> sourcesContent, final String mappings)
    {

        this.sources = sources;
        this.sourcesContent = sourcesContent;
        this.mappings = mappings;
    }
}
