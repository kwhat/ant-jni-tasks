[JNITasks](https://github.com/kwhat/jnitasks/) - Ant Tasks for JNI Projects
===========================================================================

## LD Task
Use this task to call the native linker directly.

### Parameters
| Attribute     | Type    | Default    | Description
|---------------|---------|------------|------------------------
| dir           | String  | ${basedir} | Folder to execute the task in
| jobs			| String  | auto       | Number of parallel jobs or auto for host cpu count
| toolchain     | String  | gcc        | The toolchain to use when compiling
| host          | String  | ""         | Host string used to prefix the compiler command

### Nested Elements


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
	<arg value="-lm" />
</ld>
```
