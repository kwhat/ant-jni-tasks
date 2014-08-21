[JNITasks](https://github.com/kwhat/jnitasks/) - Ant Tasks for JNI Projects
===========================================================================

## CC Task
Use this task to call the native compiler directly.

### Parameters
| Attribute     | Type    | Default    | Description
|---------------|---------|------------|------------------------
| dir           | String  | ${basedir} | Folder to execute the task in
| jobs			| String  | auto       | Number of parallel jobs or auto for host cpu count
| toolchain     | String  | gcc        | The toolchain to use when compiling
| host          | String  | ""         | Host string used to prefix the compiler command

### Nested Elements
[CcTask.Include](CCTASK.md)


### Example

```XML
<cc toolchain="clang" jobs="4" objdir="${dir.build}/obj">
	<arg value="-Wall -Wextra -Wno-unused-parameter" />
	<arg value="-fPIC" unless="native.os.isWindows" />

	<define name="DEBUG" if="ant.build.debug"/>

	<include path="${dir.build}/include" />
	<include path="${dir.src}/include" />

	<include path="${ant.build.javac.include}" />
	<include path="${ant.build.javac.include}/win32" if="native.os.isWindows"/>
	<include path="${ant.build.javac.include}/${ant.build.native.os}" unless="native.os.isWindows"/>

	<fileset dir="${dir.src}/jni">
		<include name="**/*.c" />
	</fileset>
</cc>
```
