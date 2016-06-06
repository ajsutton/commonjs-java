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
        if (requestedId.substring(0, 2) == "./") {
            var relativePath = requestedId.substring(2);
            if (moduleId.indexOf('/') > 0) {
                return moduleId.substring(0, Math.max(0, moduleId.lastIndexOf('/'))) + '/' + relativePath;
            } else {
                return relativePath;
            }
        } else if (requestedId.substring(0, 3) == "../") {
            var moduleIdParts = moduleId.split('/');
            moduleIdParts.pop();
            while (requestedId.substring(0, 3) == "../") {
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