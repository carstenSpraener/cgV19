#!/bin/bash

case `uname` in
  Darwin)
    export VP_PLUGIN_DIR="${HOME}/Library/Application Support/VisualParadigm/plugins"
    export VP_HOME="/Applications/Visual Paradigm.app/Contents/Resources/app"
    ;;
  Linux)
    export VP_PLUGIN_DIR="${HOME}/.config/VisualParadigm/plugins"
    export VP_HOME="${HOME}/Visual_Paradigm_CE_17.0"
    ;;
  MINGW*)
    export VP_PLUGIN_DIR="${HOME}/AppData/Roaming/VisualParadigm/plugins"
    export VP_HOME="/C/Program Files/Visual Paradigm CE 17.0"
    ;;
  Cygwin)
    ;;
esac

echo "***************************************************************************"
echo "* Using:"
echo "*     VP_PLUGIN_DIR=${VP_PLUGIN_DIR}"
echo "*     VP_HOME=${VP_HOME}"
echo "*"
echo "***************************************************************************"

PLUGIN="de.spraener.nextgen.vpplugin"

(cd ..; gradle :cgv19-vpplugin:clean :cgv19-vpplugin-cartridge:jar :cgv19-vpplugin:install);
mkdir -p ./build/${PLUGIN}/lib
mkdir -p ./build/${PLUGIN}/classes

cp ./build/install/cgv19-VPPlugin/lib/*.jar ./build/${PLUGIN}/lib
rm ./build/${PLUGIN}/lib/cgv19-VPPlugin*.jar
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

mkdir -p "$VP_PLUGIN_DIR/${PLUGIN}"
cp -r build/${PLUGIN}/* "$VP_PLUGIN_DIR/${PLUGIN}/"
