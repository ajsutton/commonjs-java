(function(moduleDefinitions, requestedModules) {
    var moduleCache = {};
    function require(moduleId) {
        if (moduleId === undefined || moduleId === null) {
            return undefined;
        }
        if (moduleCache[moduleId]) {
            return moduleCache[moduleId];
        }
        return defineModule(moduleId);
    }

    function defineModule(moduleId) {
        var module = { id: moduleId, exports: {} };
        moduleCache[moduleId] = module.exports;
        moduleDefinitions[moduleId].call(undefined, module, module.exports, function(requestedId) {
            return require(resolveModuleId(requestedId, moduleId));
        });
        moduleCache[moduleId] = module.exports;
        return module.exports;
    }

    function resolveModuleId(requestedId, moduleId) {
        if (requestedId === null || requestedId === undefined) {
            return undefined;
        }
        if (requestedId.startsWith("./")) {
            return moduleId.substring(0, Math.max(0, moduleId.lastIndexOf('/'))) + '/' + requestedId.substring(2);
        } else if (requestedId.startsWith("../")) {
            var moduleIdParts = moduleId.split('/');
            moduleIdParts.pop();
            while (requestedId.startsWith("../")) {
                requestedId = requestedId.substring("../".length);
                moduleIdParts.pop();
            }
            return moduleIdParts.join('/') + requestedId;
        }
        return requestedId;
    }

    for (var i = 0; i < requestedModules.length; i++) {
        require(requestedModules[i]);
    }
})