package de.spraener.nxtgen.jp;


import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.jp.cartridge.JavaPoetTestCartridge;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaPoetUsingCartridgeTests {
    private JavaPoetTestCartridge uut = new JavaPoetTestCartridge();

    @Test
    void testJavaPoetCartridgePoJo() {
        OOModel model = createModel();

        List<CodeGeneratorMapping> mappingList = uut.mapGenerators(model);
        for( CodeGeneratorMapping mapping : mappingList ) {
            CodeGenerator codeGen = mapping.getCodeGen();
            ModelElement me = mapping.getGeneratorBaseELement();
            CodeBlock cb = codeGen.resolve(me, "");
            assertTrue( cb instanceof JavaPoetRootCodeBlock);
            assertThat(cb.toCode())
                    .containsIgnoringWhitespaces("""
                            // THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
                            package pkg;
                            
                            class APoJo {
                               private String name;
                             
                               public void setName(String name) {
                                 this.name = value;;
                               }
                             
                               public String setName() {
                                 return this.name;;
                               }
                             }
                            """);
        }
    }

    @Test
    void testJavaPoetCartridgeEntity() {
        OOModel model = createModel();
        MClass entity = createClass(model, "pkg", "AEntity", "Entity");
        entity.createAttribute("name", "String");

        List<CodeGeneratorMapping>mappingList = uut.mapGenerators(model);
        assertEquals(2, mappingList.size());
        for( CodeGeneratorMapping mapping : mappingList ) {
            if( !mapping.getGeneratorBaseELement().equals(entity) ) {
                continue;
            }
            String code = mapping.getCodeGen().resolve(entity, "").toCode();
            assertThat(code)
                    .contains("""
                            //THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
                            package pkg;
                                                        
                            import javax.persistence.Column;
                            import javax.persistence.Entity;
                            import javax.persistence.Table;
                                                        
                            @Entity
                            @Table(
                                name = "aentity"
                            )
                            class AEntity {
                              @Column
                              private String name;
                                                        
                              public void setName(String name) {
                                this.name = value;;
                              }
                                                        
                              public String setName() {
                                return this.name;;
                              }
                            }
                            """);
        }
    }

    private static OOModel createModel() {
        OOModel model = new OOModel();

        MPackage pkg = new MPackage();
        pkg.setName("pkg");
        pkg.setModel(model);
        model.addModelElement(pkg);

        MClass clazz = createClass(model, "pkg", "APoJo", "PoJo");
        clazz.createAttribute("name", "String");
        return model;
    }

    private static MClass createClass(OOModel model, String pkgName, String className, String... stereotypes) {
        MPackage pkg = (MPackage)ModelHelper.findInStream(model.getModelElements().stream(), me -> me instanceof MPackage && me.getName().equals(pkgName));
        MClass entity = pkg.createMClass(className);
        entity.setModel(model);
        if( stereotypes != null ) {
            for( String stereotype : stereotypes ) {
                entity.addStereotypes(new StereotypeImpl(stereotype));
            }
        }
        return entity;
    }


}
