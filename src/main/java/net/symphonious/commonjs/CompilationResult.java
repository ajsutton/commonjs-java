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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The result of JavaScript compilation. Provides access to the JavaScript and source map.
 */
public class CompilationResult
{
    private final String javaScript;
    private final String sourceMap;

    CompilationResult(final String javaScript, final String sourceMap)
    {
        this.javaScript = javaScript;
        this.sourceMap = sourceMap;
    }

    public String getJavaScript()
    {
        return javaScript;
    }

    public String getJavaScriptWithSourceMapUrl(final String sourceMapUrl)
    {
        return javaScript + "\n//# sourceMappingURL=" + sourceMapUrl;
    }

    public String getJavaScriptWithEmbeddedSourceMap()
    {
        return getJavaScriptWithSourceMapUrl("data:application/json;base64," + Base64.getEncoder().encodeToString(sourceMap.getBytes(StandardCharsets.UTF_8)));
    }

    public String getSourceMap()
    {
        return sourceMap;
    }
}
