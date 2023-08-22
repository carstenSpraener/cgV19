package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

@CGV19Cartridge("MetaCartridge")
public class MetaCartridge extends MetaCartridgeBase {

    public static final String NAME = "MetaCartridge";

    public static final String STEREOTYPE_NAME = "Stereotype";
    public static final String STEREOTYPE_MODEL_ROOT = "ModelRoot";
    public static final String STEREOTYPE_ENUM = "StereotypeEnum";
    public static final String STEREOTYPE_CODE_GENERATOR = "CodeGenerator";
    public static final String STEREOTYPE_TRANSFORMATION = "Transformation";
    public static final String STYPE_GROOVY_SCRIPT = "GroovyScript";
    public static final String TV_TEMPLATE_SCRIPT = "templateScript";
    public static final String TV_SCRIPT_FILE = "scriptFile";
    public static final String TV_GENERATOR_CLASS = "generatorClass";
    public static final String TV_REQUIRED_STEREOTYPE = "requiredStereotype";
    public static final String STYPE_CGV19CARTRIDGE = "cgV19Cartridge";
    public static final String STYPE_CGV19CARTRIDGE_BASE = "cgV19CartridgeBase";
    public static final String TV_PRIORITY = "priority";
    public static final String STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION = "cgV19CartridgeServiceDefinition";
    public static final String TV_CARTRIDGE_CLASS = "cartridgeClass";

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> txList = super.getTransformations();
        txList.add(new GeneratorGapTransformation(
                (mc)->StereotypeHelper.hasStereotype(mc, STEREOTYPE_TRANSFORMATION)
        ));
        return txList;
    }

    @Override
    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName) {
        if (me instanceof MClass && ((MClass) me).hasStereotype(STEREOTYPE_NAME)) {
            return CodeGeneratorMapping.create(me,
                    new StereotypeDocGenerator(
                            cb -> cb.setToFileStrategy(new DocumentationOutputFileStrategy(me, "doc/stereotypes", "md"))
                    )
            );
        }
        if (me instanceof MClass && ((MClass) me).hasStereotype(STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION)) {
            return CodeGeneratorMapping.create(me,new CartridgeServiceLocaterGenerator(
                    c -> c.setToFileStrategy(
                            () -> new File(NextGen.getWorkingDir()+"/src/main/resources/META-INF/services/de.spraener.nxtgen.Cartridge")
                    )
            ));
        }
        if (me instanceof MClass mc && ((MClass) me).hasStereotype(STYPE_GROOVY_SCRIPT)) {
            return CodeGeneratorMapping.create(me,new GroovyScriptGenerator(
                    c -> c.setToFileStrategy(
                            () -> {
                                String groovyScriptName = mc.getTaggedValue(MetaCartridge.STYPE_GROOVY_SCRIPT, MetaCartridge.TV_SCRIPT_FILE);
                                return new File("src/main/resources/"+groovyScriptName);
                            })
            ));
        }
        if( me instanceof MClass && stereotypeName.equals(MetaStereotypes.STEREOTYPEDESCRIPTOR.getName())) {
            return CodeGeneratorMapping.create(me, new StereotypeDescriptorJsonGenerator(
                    (c)->c.setToFileStrategy(
                            () -> new File(NextGen.getWorkingDir()+"/doc/stereotypes.json")
                    )
            ));
        }
        return super.createMapping(me, stereotypeName);
    }
}
