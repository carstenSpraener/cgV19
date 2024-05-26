import de.spraener.nxtgen.NextGen
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.meta.MetaCartridge
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.php.PhpCodeBlock
import de.spraener.nxtgen.php.PhpHelper


String readTaggedValue( MClass mClass, String stereotype, String taggedValue, String defaultValue) {
    Stereotype sType = StereotypeHelper.getStereotype(mClass, stereotype);
    String value = sType.getTaggedValue(taggedValue);
    if( value == null || "".equals(value) ) {
        return defaultValue;
    }
    return value;
}

String getOutputType( MClass mClass) {
    return readTaggedValue(mClass, "CodeGenerator", "outputType", "OTHER")
}

String getOutputTypeEnum( MClass mClass) {
    return readTaggedValue(mClass, "CodeGenerator", "outputType", "other").toUpperCase();
}

String getOutputTo( MClass mClass) {
    return readTaggedValue(mClass, "CodeGenerator", "outputTo", "src-gen")
}
String getOutputToEnum( MClass mClass) {
    String outputTo = readTaggedValue(mClass, "CodeGenerator", "outputTo", "SRC_GEN")
    outputTo = outputTo.toUpperCase().replace('-', '_');
    return outputTo;
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

String getOutputDir( MClass mClass) {
    String rootDir = StereotypeHelper.getStereotype(mClass, MetaCartridge.STEREOTYPE_CODE_GENERATOR).getTaggedValue("outputRootDir");
    if( rootDir != null ) {
        return rootDir;
    } else {
        return "";
    }
}

String applyModifiers(String cbName) {
"""
        if( codeBlockModifiers!=null ) {
            for (Consumer<CodeBlock> codeBlockModifier: this.codeBlockModifiers) {
                codeBlockModifier.accept(${cbName});
            }
        }
"""
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
${applyModifiers("jcb")}
        return jcb;"""
            break;
        case "PHP":
            return """${metaType} me = (${metaType})element;
        String outDir = de.spraener.nxtgen.php.PhpHelper.readOutDirFromModelElement(element, "src");
        ${PhpCodeBlock.class.getName()} phpCB = new ${PhpCodeBlock.class.getName()}(outDir, 
                                                    PhpHelper.toPhpPackageName(me), 
                                                    me.getName()
                                                );
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("${mClass.getPackage().getName()}", me, "${getTemplateFileName(mClass)}");
        phpCB.addCodeBlock(gcb);
${applyModifiers("phpCB")}
        return phpCB;
"""
            break;
        case "TypeScript":
            return """GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("typscript", element, "${getTemplateFileName(mClass)}");
        gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.TypeScriptFileStrategy("angular/src/app/model", element.getName()));
${applyModifiers("gcb")}
        return gcb;
"""
            break;
        case "xml":
        case "XML":
        case "Xml":
            return """GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("xml", element, "${getTemplateFileName(mClass)}");
        gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.XmlFileStrategy("${getOutputDir(mClass)}", element));
${applyModifiers("gcb")}
        return gcb;
"""
            break;
        case "other":
        default:
             return """GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("", element, "${getTemplateFileName(mClass)}");
        gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.GeneralFileStrategy(".", "", ""));
${applyModifiers("gcb")}
        return gcb;
"""
            break;
    }
}
MClass mClass = ((MClass)this.getProperty("modelElement"));
String outputType = getOutputType(mClass)

"""// ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import java.util.function.Consumer;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.php.PhpHelper;

/*
@CGV19Generator(
        requiredStereotype = "${getRequiredStereotype(mClass)}",
        operatesOn = ${getGeneratesOn(mClass)}.class,
        outputType = OutputType.${getOutputTypeEnum(mClass)},
        outputTo = OutputTo.${getOutputToEnum(mClass)},
        templateName = "${getTemplateFileName(mClass)}",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
*/
public class ${mClass.getName()} implements CodeGenerator {
    private Consumer<CodeBlock>[] codeBlockModifiers;

    public ${mClass.getName()}(Consumer<CodeBlock>... codeBlockModifiers) {
        this.codeBlockModifiers = codeBlockModifiers;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        ${getCodeBlockDefinition(mClass, outputType)}
    }
}

"""
