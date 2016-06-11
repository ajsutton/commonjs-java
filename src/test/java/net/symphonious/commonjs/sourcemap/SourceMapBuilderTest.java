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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SourceMapBuilderTest
{

    private final SourceMapBuilder generator = new SourceMapBuilder();

    @Test
    public void shouldGenerateSourceMapHeader()
    {
        assertThat(getSourceMap().getAsJsonPrimitive("version").getAsInt(), is(3));
    }

    @Test
    public void shouldListSourceFiles()
    {
        generator.appendModule("module1", "content1");
        generator.appendModule("path/module2", "content2");
        final JsonObject sourceMap = getSourceMap();
        assertThat(sourceMap.getAsJsonArray("sources").get(0).getAsString(), is("module1.js"));
        assertThat(sourceMap.getAsJsonArray("sources").get(1).getAsString(), is("path/module2.js"));
        assertThat(sourceMap.getAsJsonArray("sourcesContent").get(0).getAsString(), is("content1"));
        assertThat(sourceMap.getAsJsonArray("sourcesContent").get(1).getAsString(), is("content2"));
    }

    @Test
    public void shouldEscapeSpecialCharactersInSourcesContent()
    {
        final String moduleSource = "alert(\"Hello world!\");\n" +
                                    "return '\\';";
        generator.appendModule("module", moduleSource);

        assertThat(getSourceMap().getAsJsonArray("sourcesContent").get(0).getAsString(), is(moduleSource));
    }

    @Test
    public void shouldGenerateMappings() throws Exception
    {
        final String file = "console.error(\"File1 Line1\");\n" +
                              "console.error(\"File1 Line 2\");\n";
        final String file2 = "console.error(\"File2 Line 1\");\n" +
                            "console.error(\"File2 Line 2\");\n";
        final String file3 = "console.error(\"File3 Line 1\");\n" +
                             "//# sourceMappingURL=test.js.map";
        generator.skipLine();
        generator.skipLine();
        generator.skipLine();
        generator.appendModule("file", file);
        generator.skipLine();
        generator.appendModule("file2", file2);
        generator.skipLine();
        generator.appendModule("file3", file3);
        assertThat(getSourceMap().getAsJsonPrimitive("mappings").getAsString(), is("A;A;A;AAAA;AACA;AACA;A;ACFA;AACA;AACA;A;ACFA;AACA;"));
    }

    private JsonObject getSourceMap()
    {
        final JsonParser jsonParser = new JsonParser();
        final String sourceMap = generator.generateSourceMap();
        return jsonParser.parse(sourceMap).getAsJsonObject();
    }
}