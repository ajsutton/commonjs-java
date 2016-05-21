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

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.StringLiteral;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

class DependencyFinder
{
    private final CompilerEnvirons environs;

    public DependencyFinder()
    {
        environs = new CompilerEnvirons();
        environs.setRecoverFromErrors(true);
    }

    public Collection<String> findDependencies(final ModuleInfo moduleInfo)
    {
        final Collection<String> dependentModuleIds = new HashSet<>();
        final IRFactory irFactory = new IRFactory(environs);
        final AstRoot astRoot = irFactory.parse(moduleInfo.getSource(), moduleInfo.getModuleId(), 0);
        astRoot.visit(node -> {
            if (node instanceof FunctionCall)
            {
                getDependentModuleId((FunctionCall) node, moduleInfo.getModuleId()).ifPresent(dependentModuleIds::add);
            }
            return true;
        });
        return dependentModuleIds;
    }

    public Optional<String> getDependentModuleId(final FunctionCall functionCall, final String currentModuleId)
    {
        if (functionCall.getTarget() instanceof Name)
        {
            final Name functionName = (Name) functionCall.getTarget();
            if (functionName.getIdentifier().equals("require") &&
                 functionCall.getArguments().size() == 1 &&
                 functionName.getDefiningScope() == null)
            {
                final AstNode argument = functionCall.getArguments().get(0);
                if (argument instanceof StringLiteral)
                {
                    return Optional.of(resolveModuleId(((StringLiteral) argument).getValue(), currentModuleId));
                }
            }
        }
        return Optional.empty();
    }

    private String resolveModuleId(final String requestedModule, final String currentModuleId)
    {
        if (requestedModule.startsWith("./"))
        {
            return currentModuleId.substring(0, Math.max(0, currentModuleId.lastIndexOf('/'))) + "/" + requestedModule.substring("./".length());
        }
        else if (requestedModule.startsWith("../"))
        {
            final String[] currentModuleParts = currentModuleId.split("/");
            int partsToUse = currentModuleParts.length - 1;
            String moduleSuffix = requestedModule;
            while (moduleSuffix.startsWith("../"))
            {
                partsToUse--;
                moduleSuffix = moduleSuffix.substring("../".length());
            }
            return Arrays.stream(currentModuleParts, 0, Math.min(0, partsToUse)).collect(Collectors.joining("/")) + moduleSuffix;
        }
        return requestedModule;
    }
}
