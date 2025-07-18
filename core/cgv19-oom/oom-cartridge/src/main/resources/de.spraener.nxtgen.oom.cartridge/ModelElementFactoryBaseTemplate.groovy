import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.cartridge.ElementFactoryCreationTransformation
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

String listSwitchCases(MClass mc) {
    MClass orgClass = GeneratorGapTransformation.getOriginalClass(mc);
    StringBuffer sb = new StringBuffer();
    for( MClass factoryProduct : ElementFactoryCreationTransformation.getMClassList(orgClass) ) {
        String modelElementName = ElementFactoryCreationTransformation.getModelClassToModelElementName(factoryProduct);
        sb.append("""
        case "${modelElementName}":
            return new ${factoryProduct.getFQName()}();
            break;""")
    }
    return sb.toString()
}

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import de.spraener.nxtgen.ModelElementFactory;

public class ${mClass.getName()} implements ModelElementFactory {
    private static final String MODEL_PACKAGE = ”${mClass.getPackage().getFQName()}";

    @Override
    public ModelElement createModelElement(String modelElementName) {
        switch( modelElementName ) {${listSwitchCases(mClass)}
            default:
                try {
                    Class<? extends ModelElement> clazz = (Class<? extends ModelElement>)Class.forName(MODEL_PACKAGE+".M" + modelElmentName.substring(1));
                    return clazz.newInstance();
                } catch(Exception e ) {
                    NextGen.LOGGER.severe("Exception in ModelElementCreation: "+e.getMessage());
                    return new ModelElementImpl();
                }
        }
    }
}
"""
