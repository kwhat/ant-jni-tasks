[ant-jnitasks](https://github.com/kwhat/ant-jnitasks/) - Ant Tasks for Compiling Native Code in JNI Projects
====================================================================================

## About
JNITasks are a set of simple Ant tasks to reliably build C/C++ code for Java Native Interface (JNI) projects as well as any native library dependencies that maybe required.  The goal of the project is to provide a spmlified, working alternative to the legacy ant-contrib [cpptasks](http://ant-contrib.sourceforge.net/cpptasks/index.html) allowing C/C++ code to be configured and built directly from Ant as part of the Java build process.  No external shell, bat or ps1 shell scripts to bootstrap the process and no repetitive exec commands all over your build.xml file.

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
* [autoreconf](doc/AUTORECONF.md)
* [configure](doc/CONFIGURE.md)
* [make](doc/MAKE.md)
* [pkg-config](doc/PKGCONFIG.md)
* [cc](doc/CC.md)
* [ld](doc/LD.md)

The following are projects that currently use JNITasks that you may use as an example.

[JNativeHook](https://github.com/kwhat/jnativehook/)
