#!/bin/bash
VP_PLUGIN_DIR="${HOME}/Library/Application Support/VisualParadigm/plugins"
PLUGIN="de.spraener.nextgen.vpplugin"

(cd ..; gradle :cgV19-VPPlugin:clean :cgV19-VPPluginCartridge:jar :cgV19-VPPlugin:install);
mkdir -p ./build/${PLUGIN}/lib
mkdir -p ./build/${PLUGIN}/classes

cp ./build/install/cgV19-VPPlugin/lib/*.jar ./build/${PLUGIN}/lib
rm ./build/${PLUGIN}/lib/cgV19-VPPlugin*.jar
rm ./build/${PLUGIN}/lib/openapi.jar
rm ./build/${PLUGIN}/lib/slf4j*.jar

cp -r ./build/classes/java/main/* ./build/${PLUGIN}/classes
cp ./src/main/assets/* ./build/${PLUGIN}

for jar in `(cd ./build/${PLUGIN}/lib; ls *.jar)`; do
  echo "        <library path=\"lib/$jar\" relativePath=\"true\"/>"
done

if [ -d "$VP_PLUGIN_DIR/${PLUGIN}" ]; then
    rm -rf "$VP_PLUGIN_DIR/${PLUGIN}"
fi

mkdir "$VP_PLUGIN_DIR/${PLUGIN}"
cp -r build/${PLUGIN}/* "$VP_PLUGIN_DIR/${PLUGIN}/"
