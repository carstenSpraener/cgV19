package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

import java.io.File;

public class MetaCartridge extends MetaCartridgeBase {
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
    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName) {
        if (me instanceof MClass && ((MClass) me).hasStereotype(STEREOTYPE_NAME)) {
            return CodeGeneratorMapping.create(me,
                    new StereotypeDocGenerator(
                            cb -> cb.setToFileStrategy(new DocumentationOutputFileStrategy(me, "doc/stereotypes", "md"))
                    )
            );
        }
        if (me instanceof MClass && ((MClass) me).hasStereotype(STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION)) {
            return CodeGeneratorMapping.create(me,
                    new CartridgeServiceLocaterGenerator(
                            cb -> cb.setToFileStrategy(() -> new File("src/main/resources/META-INF/services/de.spraener.nxtgen.Cartridge")
                        )
                    )
            );
        }
        return super.createMapping(me, stereotypeName);
    }
}
