[JNITasks](https://github.com/kwhat/jnitasks/) - Ant Tasks for JNI Projects
===========================================================================

## About
JNITasks provide a simple and reliable way to build JNI dependencies for Java projects.

*Please note that this project is still very new.  The only available documentation at this time is the source code. Feel
free to help correct that problem.*

### Supported Toolchains
* GNU C Compiler
* GNU C++ Compiler

### Supported Build Systems
* GNU Autotools

Need support for additional features, toolchains or build systems?  Please file a feature request bug or better yet,
create a branch and develop your own!

## Usage
Building JNI libraries is complex and an often difficult task for most Java programmers.  I have outlined a few steps to
help guide you though the process.

### Code Structure

How you structure your code will directly affect how difficult it will be to debug and maintain your project.  Simple
problems in your native code can produce obscure JVM errors and segfaults that are very difficult to debug or even
reproduce.  Because of this, I strongly recommend that you start by creating a stand-alone native library and some type
of entry point to use for testing.  You may choose to write this portion of code in any language, or combination
of languages, that can be compiled to a native library.  If your library leverages platform-specific or
configuration-dependent libraries, I highly recommend using an additional build system such as autotools to do most of
the heavy lifting for you.  Please note that this newly created library should not include any JNI dependent code.

Only after you are confident that your native code is functioning correctly should you start developing the JNI portion
of your library.  Think of your JNI code as a wrapper to your native library that makes native API calls and leverages
JNI to translate the result of those calls to something the JVM can understand.  This is the only place in your code
where you should be accessing Java related resources or any portion of the JVM.  **Do not under any circumstance store
platform-dependent data like pointers in Java.**  You will inadvertently create a sandbox violation that may be
used to crash your application and/or execute arbitrary code.  Instead, either create a local native data type to store
the value or a Java Object that contains a Java-safe copy of the data before returning it to the JVM.  You may write
your JNI wrapper in either ANSI C99 (or later) or C++.  It may be possible to write JNI code in other languages and/or
standards, however I strongly discourage their use for portability reasons.

### Compiling

Compiling should be done in the following order:
1. Start by compiling all of the dependent libraries needed using the build system tools provided by JNITasks.
2. Compile all Java code including code dependent on native methods.
3. Use Ant's javah task to produce a native header for your JNI code.
4. Compile your JNI code using either the cc task.
5. Link your compiled object files against your native library as either a dynamic or static library.

## Example Projects
The following are projects that currently use JNITasks that you may use as an example.

[JNativeHook](https://github.com/kwhat/jnativehook/)

If you would like your project listed here, please email me.
