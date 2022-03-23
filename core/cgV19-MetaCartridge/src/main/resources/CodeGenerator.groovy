import de.spraener.nxtgen.NextGen
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.meta.MetaCartridge
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass


String readTaggedValue( MClass mClass, String stereotype, String taggedValue, String defaultValue) {
    Stereotype sType = StereotypeHelper.getStereotype(mClass, stereotype);
    String value = sType.getTaggedValue(taggedValue);
    if( value == null || "".equals(value) ) {
        return defaultValue;
    }
    return value;
}

String getOutputType( MClass mClass) {
    return readTaggedValue(mClass, "CodeGenerator", "outputType", "none")
}

String getOutputTo( MClass mClass) {
    return readTaggedValue(mClass, "CodeGenerator", "outputTo", "src-gen")
}

String getGeneratesOn(mClass) {
    return readTaggedValue(mClass, "CodeGenerator", "generatesOn", "ModelElement")
}

String getTemplateFileName(MClass mClass) {
    return readTaggedValue(mClass,"CodeGenerator", "templateScript", "/${mClass.getName()}.groovy")
}

String getRequiredStereotype(MClass mClass) {
    return readTaggedValue(mClass,"CodeGenerator", "requiredStereotype", "not-found")
}

String getPhpRootPackage(MClass mClass) {
    String sType = getRequiredStereotype(mClass);
    if( sType.contains("Controller") ) {
        return "Controller";
    } else if (sType.equals("Entity") ) {
        return "Entity"
    } else if(sType.equals("Repository")) {
        return "Repository"
    }
    NextGen.LOGGER.warning("MClass "+mClass.getName()+" has no known generateOn="+sType);
    return "Unknonw";
}

String getOutputDir( MClass mClass) {
    String rootDir = StereotypeHelper.getStereotype(mClass, MetaCartridge.STEREOTYPE_CODE_GENERATOR).getTaggedValue("outputRootDir");
    if( rootDir != null ) {
        return rootDir;
    } else {
        return "";
    }
}

String getCodeBlockDefinition(MClass mClass, String outputType) {
    String outputTo = "src".equals(getOutputTo(mClass)) ? "src/main/java" : "src/main/java-gen"
    String metaType = getGeneratesOn(mClass)
    switch( outputType ) {
        case "Java":
            return """${metaType} me = (${metaType})element;
        JavaCodeBlock jcb = new JavaCodeBlock("${outputTo}", me.getPackage().getFQName(), me.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("${mClass.getName()}", me, "${getTemplateFileName(mClass)}");
        jcb.addCodeBlock(gcb);
        return jcb;"""
            break;
        case "PHP":
            return """${metaType} me = (${metaType})element;
        de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock phpCB = new de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock("src", "${getPhpRootPackage(mClass)}", me.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("${mClass.getPackage().getName()}", me, "${getTemplateFileName(mClass)}");
        phpCB.addCodeBlock(gcb);
        return phpCB;
"""
            break;
        case "TypeScript":
            return """GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("typscript", element, "${getTemplateFileName(mClass)}");
        gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.TypeScriptFileStrategy("angular/src/app/model", element.getName()));
        return gcb;
"""
            break;
        case "xml":
        case "XML":
        case "Xml":
            return """GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("xml", element, "${getTemplateFileName(mClass)}");
        gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.XmlFileStrategy("${getOutputDir(mClass)}", element));
        return gcb;
"""
            return
            break;
        default:
            return """// TODO: Implement the creation of a CodeBlock"
        return null;
"""

            break;
    }
}
MClass mClass = ((MClass)this.getProperty("modelElement"));
String outputType = getOutputType(mClass)

"""// ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;

public class ${mClass.getName()} implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        ${getCodeBlockDefinition(mClass, outputType)}
    }
}

"""
