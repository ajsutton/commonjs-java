## commonjs-java

A java library to compile commonjs module and their dependencies into a single bundle, ready for
use in the browser (or any other JavaScript runtime).

### Why?

There are a number of utilities that can perform this same (or similar) function as part of your
build workflow - browserify, requirejs (for AMD modules), webpack etc. Those modules are brilliant
for most use-cases and you should use them. commonjs-java was built to make it easier to provide
full hot-deploy of JavaScript resources from a Java webapp. Instead of bundling the JavaScript
together during a build step, commonjs-java can be integrated directly into the webapp and combine
the JavaScript on the fly.

It's also an interesting thing to work out how to do...

### License

Licensed under the Apache License, Version 2.0.

### Maintainer

[Adrian Sutton](https://www.symphonious.net/)
