//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.cartridges.AnnotatedCartridgeImpl;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;

import java.util.List;
import java.util.ArrayList;

public class MetaCartridgeBase extends AnnotatedCartridgeImpl {

    @Override
    public String getName() {
        return "MetaCartridgeBase";
    }
    
    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> result = super.getTransformations();
        result.add( new de.spraener.nxtgen.cartridge.meta.RemoveModelRootPackage() );
        result.add( new de.spraener.nxtgen.cartridge.meta.EnsureGeneratorDefinitionsTransformation() );
        result.add( new de.spraener.nxtgen.cartridge.meta.EnsureTransformationDefinitionsTransformation() );
        result.add( new de.spraener.nxtgen.cartridge.meta.CartridgeServicesLocatorTransformation() );
        result.add( new de.spraener.nxtgen.cartridge.meta.CartridgeBaseForCartridgeTransformation() );
        result.add( new de.spraener.nxtgen.cartridge.meta.StereotypeEnumToDescriptorTransformation() );

        return result;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for( ModelElement me : m.getModelElements() ) {
            if( me.getStereotypes().isEmpty() ) {
                continue;
            }
            if( StereotypeHelper.hasStereotype(me, "GroovyScript") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "GroovyScript");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.GroovyScriptGenerator());
                    }
                    mapping.setStereotype("GroovyScript");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "cgV19CartridgeBase") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "cgV19CartridgeBase");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.CartridgeBaseGenerator());
                    }
                    mapping.setStereotype("cgV19CartridgeBase");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "Transformation") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "Transformation");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.TransformationGenerator());
                    }
                    mapping.setStereotype("Transformation");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "cgV19Cartridge") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "cgV19Cartridge");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.CartridgeImplGenerator());
                    }
                    mapping.setStereotype("cgV19Cartridge");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "StereotypeDescriptor") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "StereotypeDescriptor");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.StereotypeDescriptorJsonGenerator());
                    }
                    mapping.setStereotype("StereotypeDescriptor");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "StereotypeEnum") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "StereotypeEnum");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.StereotypeEnumGenerator());
                    }
                    mapping.setStereotype("StereotypeEnum");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "cgV19CartridgeServiceDefinition") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "cgV19CartridgeServiceDefinition");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.CartridgeServiceLocaterGenerator());
                    }
                    mapping.setStereotype("cgV19CartridgeServiceDefinition");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "Stereotype") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "Stereotype");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.StereotypeDocGenerator());
                    }
                    mapping.setStereotype("Stereotype");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "CodeGenerator") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "CodeGenerator");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.CodeGeneratorGenerator());
                    }
                    mapping.setStereotype("CodeGenerator");
                    result.add(mapping);
                }
            }
            if( StereotypeHelper.hasStereotype(me, "TransformationBase") ) {
                CodeGeneratorMapping mapping = null;
                if( me instanceof MClass tME ) {
                    mapping = createMapping(tME, "TransformationBase");
                    if (mapping == null) {
                        mapping = CodeGeneratorMapping.create(me, new de.spraener.nxtgen.cartridge.meta.TransformationBaseGenerator());
                    }
                    mapping.setStereotype("TransformationBase");
                    result.add(mapping);
                }
            }

        }

        List<CodeGeneratorMapping> annotatedGeneratorMappings = super.mapGenerators(m);
        for( CodeGeneratorMapping mapping : annotatedGeneratorMappings ) {
            if( !result.contains(mapping) ) {
                result.add(mapping);
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

