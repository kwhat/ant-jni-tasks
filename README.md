[JNITasks](https://github.com/kwhat/jnitasks/) - Ant Tasks for JNI Projects
===========================================================================

## About
JNITasks are a set if simple Ant tasks to reliably build Java Native Interface (JNI) projects and their dependencies.

### Supported Toolchains
* GNU C Compiler
* GNU C++ Compiler
* LLVM CLANG Compiler
* LLVM CLANG++ Compiler

### Supported Build Systems
* GNU Autotools
* GNU Make

Need support for additional features, toolchains or build systems?  Please file a feature request bug.

## Usage
Simply add the following to your ant project:

```XML
<typedef resource="org/jnitasks/antlib.xml" classpath="path/to/JNITasks.jar" />
```

## Available Tasks

[autoreconf](doc/AUTORECONFIG.md)
[configure](#)
[make](#)
[pkg-config](#)
[cc](#)
[ld](#)

The following are projects that currently use JNITasks that you may use as an example.

[JNativeHook](https://github.com/kwhat/jnativehook/)
