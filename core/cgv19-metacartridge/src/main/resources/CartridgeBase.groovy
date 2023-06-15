import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.meta.CartridgeGeneratorHelper
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = (MClass)this.getProperty("modelElement");

String addTransformations(MClass mClass, String listName ) {
    StringBuffer sb = new StringBuffer();
    for( MClass transformation  : CartridgeGeneratorHelper.listTransformations((OOModel)mClass.getModel()) ) {
        sb.append("        ");
        sb.append(listName).append(".add( new ")
        sb.append(transformation.getFQName());
        sb.append("() );\n")
    }
    return sb.toString();
}

String listCodeGeneratorMappings(MClass mClass, String me, String resutListName) {
    StringBuffer sb = new StringBuffer();
    Map<String,List<MClass>> codeGenertorList = CartridgeGeneratorHelper.listCodeGeneratorByStereotype((OOModel)mClass.getModel());
    for( String sTypeName : codeGenertorList.keySet() ) {
        List<MClass> codeGenList = codeGenertorList.get(sTypeName);
        sb.append("${printCodeGeneratorMapping(sTypeName, codeGenList, me, resutListName)}")
    }
    return sb.toString();
}

String getGeneratesOn(MClass codeGenerator) {
    Stereotype stype = StereotypeHelper.getStereotype(codeGenerator, "CodeGenerator")
    return stype.getTaggedValue("generatesOn")
}

String printCodeGeneratorMapping(String sTypeName, List<MClass> mClasses, String meName, String listName) {
    StringBuffer sb = new StringBuffer();
    sb.append("            if( StereotypeHelper.hasStereotye(me, \"${sTypeName}\") ) {\n");
    for( MClass codeGenerator : mClasses ) {
        String generatesOn = getGeneratesOn(codeGenerator)
        if( generatesOn!=null ) {
            sb.append(
"""                if( ${meName} instanceof ${generatesOn} tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "${sTypeName}");
                    if (mapping != null) {
                        ${listName}.add(mapping);
                    } else {
                        ${listName}.add(CodeGeneratorMapping.create(${meName}, new ${codeGenerator.getFQName()}()));
                    }
                }
""")
        } else {
            sb.append("                    ${listName}.add(CodeGeneratorMapping.create(${meName}, new ${codeGenerator.getFQName()}()));\n")
        }
    }
    sb.append("            }\n");
    return sb.toString()
}

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;

import java.util.List;
import java.util.ArrayList;

public class ${mClass.getName()} implements Cartridge {

    @Override
    public String getName() {
        return \"${mClass.getName()}\";
    }
    
    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> result = new ArrayList<>();
${addTransformations(mClass, "result")}
        return result;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for( ModelElement me : m.getModelElements() ) {
${listCodeGeneratorMappings(mClass, "me", "result")}
        }
        return result;
    }



    /**
     * Use this method to override default mappings. Return null for default mapping.
     */
    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName) {
        return null;
    }
}

"""
