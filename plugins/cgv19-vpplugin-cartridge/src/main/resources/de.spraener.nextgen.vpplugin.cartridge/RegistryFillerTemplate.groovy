import de.spraener.nextgen.vpplugin.cartridge.CreateExporterRegistryFillerTransformation
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

def printFilling(MClass mClass) {
    Map<String,String> fillerMap = CreateExporterRegistryFillerTransformation.getFillerMap(mClass);
    StringBuffer sb = new StringBuffer();
    for(Map.Entry e : fillerMap ) {
        sb.append(String.format( "        exporterMap.put(\"%s\", ()-> new %s());\n", e.getKey(), e.getValue()));
    }
    return sb.toString();
}

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import java.util.Map;
import java.util.function.Supplier;

public class ${mClass.getName()} {

    public static void fillRegistry(Map<String, Supplier<Exporter>> exporterMap) {
${printFilling(mClass)}
    }
}
"""
