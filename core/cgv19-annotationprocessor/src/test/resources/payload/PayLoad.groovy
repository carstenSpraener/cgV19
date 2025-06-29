import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.apdemo.cartridge.PayloadCartridge
import de.spraener.nxtgen.model.Model
import de.spreaner.nxtgen.annoproc.APModelElementImpl

APModelElementImpl mClass = this.getProperty("modelElement");
Model model = mClass.getModel();

"""
// ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${PayloadCartridge.toPackageName(mClass);};

public class ${mClass.getName()} {
    public static String toPayload(${mClass.getProperty("srcClassName")} data) {
        return null;
    }
    
    public static ${mClass.getProperty("srcClassName")} fromPayload(String jsonString) {
        return null;
    }
}
"""