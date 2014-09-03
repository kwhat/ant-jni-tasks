[JNITasks](https://github.com/kwhat/jnitasks/) - Ant Tasks for JNI Projects
===========================================================================

## LD Task
Use this task to call the native linker directly.

### Parameters

| Attribute      | Type     | Default                | Description
|----------------|----------|------------------------|-----------------------------------------------------------------
| outfile        | File     |                        | Folder to execute the task in
| toolchain      | String   | gcc                    | The toolchain to use when compiling
| host           | String   | ""                     | Host string used to prefix the compiler command

### Nested Elements

[LDTask.LIBRARY](LD.md)

| Attribute      | Type     | Default                | Description
|----------------|----------|------------------------|-----------------------------------------------------------------
| lib            | String   |                        | Name of the library to link against
| path           | String   |                        | Path to look for the lib in

### Example

```XML
<ld toolchain="clang" outfile="${dir.lib}/${ant.build.native.executable}">
	<arg value="-dynamiclib" if="native.os.isDarwin" />
	<arg value="-shared" unless="native.os.isDarwin" />

	<arg value="-static-libgcc" if="native.os.isWindows" />

	<fileset dir="${dir.bin}">
		<include name="obj/jni/**/*.o" />
	</fileset>

	<!-- Linking order matters and libraries should come after obj files. -->
	<library lib="-lm" />
</ld>
```
