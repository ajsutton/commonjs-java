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

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SourceMapBuilder
{
    private final List<String> sources = new ArrayList<>();
    private final List<String> sourcesContent = new ArrayList<>();
    private final StringBuilder mappings = new StringBuilder();
    private int lastSourceIndex = 0;
    private int lastSourceLine = 0;

    public void appendModule(final String moduleId, final String source)
    {
        final String fileName = moduleId + ".js";
        sources.add(fileName);
        sourcesContent.add(source);

        final StringTokenizer tokenizer = new StringTokenizer(source, "\r\n");
        int line = 0;
        while (tokenizer.hasMoreTokens())
        {
            tokenizer.nextToken();
            final int sourceIndex = sources.size() - 1;
            Base64VLQ.encode(mappings, 0); // Column 0 in generated output
            Base64VLQ.encode(mappings, sourceIndex - lastSourceIndex); // source file index
            Base64VLQ.encode(mappings, line - lastSourceLine); // Starting line in source file
            Base64VLQ.encode(mappings, 0); // Column 0 in source file
            lastSourceIndex = sourceIndex;
            lastSourceLine = line;
            mappings.append(";");
            line++;
        }
    }

    public void skipLine()
    {
        Base64VLQ.encode(mappings, 0); // Column 0 in generated output, but no source file info.
        mappings.append(";");
    }

    public String generateSourceMap()
    {
        return new GsonBuilder().disableHtmlEscaping().create()
                                .toJson(new SourceMap(sources, sourcesContent, mappings.toString()));
    }
}
