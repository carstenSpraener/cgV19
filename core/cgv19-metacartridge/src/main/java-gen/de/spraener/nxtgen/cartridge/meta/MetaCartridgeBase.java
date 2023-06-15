//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;

import java.util.List;
import java.util.ArrayList;

public class MetaCartridgeBase implements Cartridge {

    @Override
    public String getName() {
        return "MetaCartridgeBase";
    }
    
    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> result = new ArrayList<>();
        result.add( new de.spraener.nxtgen.cartridge.meta.RemoveModelRootPackage() );
        result.add( new de.spraener.nxtgen.cartridge.meta.EnsureGeneratorDefinitionsTransformation() );
        result.add( new de.spraener.nxtgen.cartridge.meta.EnsureTransformationDefinitionsTransformation() );
        result.add( new de.spraener.nxtgen.cartridge.meta.CartridgeBaseForCartridgeTransformation() );
        result.add( new de.spraener.nxtgen.cartridge.meta.CartridgeServicesLocatorTransformation() );

        return result;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for( ModelElement me : m.getModelElements() ) {
            if( StereotypeHelper.hasStereotype(me, "cgV19CartridgeBase") ) {
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "cgV19CartridgeBase");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.CartridgeBaseGenerator()));
                    }
                }
            }
            if( StereotypeHelper.hasStereotype(me, "GroovyScript") ) {
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "GroovyScript");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.GroovyScriptGenerator()));
                    }
                }
            }
            if( StereotypeHelper.hasStereotype(me, "Transformation") ) {
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "Transformation");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.TransformationBaseGenerator()));
                    }
                }
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "Transformation");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.TransformationGenerator()));
                    }
                }
            }
            if( StereotypeHelper.hasStereotype(me, "cgV19Cartridge") ) {
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "cgV19Cartridge");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.CartridgeImplGenerator()));
                    }
                }
            }
            if( StereotypeHelper.hasStereotype(me, "cgV19CartridgeServiceDefinition") ) {
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "cgV19CartridgeServiceDefinition");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.CartridgeServiceLocaterGenerator()));
                    }
                }
            }
            if( StereotypeHelper.hasStereotype(me, "StereotypeEnum") ) {
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "StereotypeEnum");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.StereotypeEnumGenerator()));
                    }
                }
            }
            if( StereotypeHelper.hasStereotype(me, "Stereotype") ) {
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "Stereotype");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.StereotypeDocGenerator()));
                    }
                }
            }
            if( StereotypeHelper.hasStereotype(me, "CodeGenerator") ) {
                if( me instanceof MClass tME ) {
                    CodeGeneratorMapping mapping = createMapping(tME, "CodeGenerator");
                    if (mapping != null) {
                        result.add(mapping);
                    } else {
                        result.add(CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.CodeGeneratorGenerator()));
                    }
                }
            }

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

