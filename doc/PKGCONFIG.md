[JNITasks](https://github.com/kwhat/jnitasks/) - Ant Tasks for JNI Projects
===========================================================================

## PkgConfig Task

Use this task to retrieve information about installed libraries on the native system.


### Parameters

| Attribute      | Type     | Default                | Description
|----------------|----------|------------------------|-----------------------------------------------------------------
| outputproperty | Property |                        | Property to store library information in
| modversion     | Boolean  | False                  | Get version information for libraries
| quiet          | Boolean  | False                  | Disable error output
| cflags         | Boolean  | False                  | Queries compiler flags
| libs           | Boolean  | False                  | Queries linker flags
| libsOnlyPath   | Boolean  | False                  | Queries linker search paths
| libsOnlyLib    | Boolean  | False                  | Queries linker libraries
| uninstalled    | Boolean  | False                  | Force the use of uninstalled packages
| exists         | String   |                        | Checks for the specified library versions
| static         | Boolean  | False                  | Output libraries for static linking


### Nested Elements

[PkgConfig.Variable](PKGCONFIG.md) extends [CcTask.Define](CCTASK.md)
[DirSet](https://ant.apache.org/manual/Types/dirset.html)
[Environment.Variable](https://ant.apache.org/manual/Tasks/exec.html)


### Example

```XML
<pkgconfig libs="true" libraries="libusb" outputproperty="ant.build.native.ld.libs.usb">
	<dirset dir="${dir.build}">
		<include name="lib/pkgconfig"/>
		<include name="bin/pkgconfig"/>
	</dirset>
</pkgconfig>
```
