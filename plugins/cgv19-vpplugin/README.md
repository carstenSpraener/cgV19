# CGV19 Plugin for VisualParadigm

## IMPORTANT NOTE: Java 11 required!

Because Visual Plugin runs with java 11 this plugin also mut be build with java 11. This is a little tricky since cgv19 requires java 17 or higher.

To come around this trap, you have to build the cgv19-vpplugin-cartridge with cgv19 and java 17+.

Then generate the code for this plugin with ```cgv19 -m de.spraener.nextgen.vpplugin.oom```

If the code is generated, switch to java 11 and run the buildPlugin.sh script.
