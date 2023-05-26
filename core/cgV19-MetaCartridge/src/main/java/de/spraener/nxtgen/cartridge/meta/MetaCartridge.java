package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.*;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.ArrayList;
import java.util.List;

public class MetaCartridge extends MetaCartridgeBase {
    public static final String STEREOTYPE_NAME="Stereotype";
    public static final String STEREOTYPE_MODEL_ROOT="ModelRoot";
    public static final String STEREOTYPE_ENUM="StereotypeEnum";
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
        return "Meta-Catridge for OOM";
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for( ModelElement me : m.getModelElements() ) {
            if( me instanceof MClass && ((MClass) me).hasStereotype(STEREOTYPE_NAME)) {
                result.add(CodeGeneratorMapping.create(me, new StereotypeDocGenerator()));
            }
            if( me instanceof MClass && ((MClass) me).hasStereotype(STEREOTYPE_ENUM) ) {
                result.add(CodeGeneratorMapping.create(me, new StereotypeEnumGenerator()));
            }
            if( me instanceof MClass && ((MClass)me).hasStereotype(STEREOTYPE_TRANSFORMATION) ) {
                result.add(CodeGeneratorMapping.create(me ,new TransformationBaseGenerator()));
                result.add(CodeGeneratorMapping.create(me ,new TransformationGenerator()));
            }
            if( me instanceof MClass && ((MClass)me).hasStereotype(STEREOTYPE_CODE_GENERATOR) ) {
                result.add(CodeGeneratorMapping.create(me ,new CodeGeneratorGenerator()));
            }
            if( me instanceof MClass && ((MClass)me).hasStereotype(STYPE_GROOVY_SCRIPT) ) {
                result.add(CodeGeneratorMapping.create(me, new GroovyScriptGenerator()));
            }
            if( me instanceof MClass && ((MClass)me).hasStereotype(STYPE_CGV19CARTRIDGE_BASE) ) {
                result.add(CodeGeneratorMapping.create(me, new CartridgeBaseGenerator()));
            }
            if( me instanceof MClass && ((MClass)me).hasStereotype(STYPE_CGV19CARTRIDGE) ) {
                result.add(CodeGeneratorMapping.create(me, new CartridgeImplGenerator()));
            }
            if( me instanceof MClass && ((MClass)me).hasStereotype(STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION) ) {
                result.add(CodeGeneratorMapping.create(me, new CartridgeServiceLocaterGenerator()));
            }
        }
        return result;
    }
}
