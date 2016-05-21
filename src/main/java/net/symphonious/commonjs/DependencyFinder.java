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
import org.mozilla.javascript.ast.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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
        final Collection<String> dependentModuleIds = new ArrayList<>();
        final IRFactory irFactory = new IRFactory(environs);
        final AstRoot astRoot = irFactory.parse(moduleInfo.getSource(), moduleInfo.getModuleId(), 0);
        astRoot.visit(node -> {
            if (node instanceof FunctionCall)
            {
                getDependentModuleId((FunctionCall) node).ifPresent(dependentModuleIds::add);
            }
            return true;
        });
        return dependentModuleIds;
    }

    public Optional<String> getDependentModuleId(final FunctionCall functionCall)
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
                    return Optional.of(((StringLiteral) argument).getValue());
                }
            }
        }
        return Optional.empty();
    }
}
