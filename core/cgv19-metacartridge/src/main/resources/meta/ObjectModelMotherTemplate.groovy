import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();


"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.OOModel;

public class ${mClass.getName()} {
    // TODO: Build a default model to use in your test cases
    public static OOModel createDefaultModel() {
        return OOModelBuilder.createModel(
            m -> OOModelBuilder.createPackage(m, "my.test.model")
        );
    }
}
"""
