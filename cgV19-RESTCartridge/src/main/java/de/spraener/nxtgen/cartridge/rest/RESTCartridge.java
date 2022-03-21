/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.angular.TSTypeGenerator;
import de.spraener.nxtgen.cartridge.rest.cntrl.ControllerGenerator;
import de.spraener.nxtgen.cartridge.rest.cntrl.LogicGenerator;
import de.spraener.nxtgen.cartridge.rest.cntrl.PhpControllerBaseGenerator;
import de.spraener.nxtgen.cartridge.rest.cntrl.PhpControllerGenerator;
import de.spraener.nxtgen.cartridge.rest.entity.DDLGenerator;
import de.spraener.nxtgen.cartridge.rest.entity.EntityGenerator;
import de.spraener.nxtgen.cartridge.rest.entity.PhpEntityGenerator;
import de.spraener.nxtgen.cartridge.rest.entity.RepositoryGenerator;
import de.spraener.nxtgen.cartridge.rest.entity.PhpRepositoryGenerator;
import de.spraener.nxtgen.cartridge.rest.transformations.*;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RESTCartridge extends  RESTCartridgeBase {
    @Override
    public String getName() {
        return "REST-Cartridge";
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model model) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for( ModelElement me : model.getModelElements() ) {
            NextGen.LOGGER.info("handling model element "+me.getName());
            if( isEntity(me) ) {
                result.add(CodeGeneratorMapping.create(me, new EntityGenerator()));
                result.add(CodeGeneratorMapping.create(me, new PhpEntityGenerator()));
            } else if( isDDL(me) ) {
                result.add(CodeGeneratorMapping.create(me, new DDLGenerator()));
            } else if( isTSType(me) ) {
                result.add(CodeGeneratorMapping.create(me, new TSTypeGenerator()));
            } else if( isRepository(me) ) {
                result.add(CodeGeneratorMapping.create(me, new RepositoryGenerator()));
                result.add(CodeGeneratorMapping.create(me, new PhpRepositoryGenerator()));
            } else if( hasStereotype(RESTStereotypes.RESTCONTROLLER.getName(), me) ) {
                result.add(CodeGeneratorMapping.create(me, new ControllerGenerator()));
                result.add(CodeGeneratorMapping.create(me, new PhpControllerBaseGenerator()));
                result.add(CodeGeneratorMapping.create(me, new PhpControllerGenerator()));
            } else if( hasStereotype(RESTStereotypes.IMPL.getName(), me) ) {
                result.add(CodeGeneratorMapping.create(me, new PoJoGenerator()));
            } else if( isLogic(me) ) {
                result.add(CodeGeneratorMapping.create(me, new LogicGenerator()));
            } else if( isSprintBootApplication(me) ) {
                Logger.getGlobal().info(()->"Erzeuge SpringBoot-Application");
                System.err.println("Erzeuge SpringBoot-Application");
                result.add(CodeGeneratorMapping.create(me, new SpringBootAppGenerator()));
            }
        }
        return result;
    }

    private boolean isSprintBootApplication(ModelElement me) {
        return StereotypeHelper.hasStereotye(me, RESTStereotypes.SPRINGBOOTAPP.getName());
    }

    private boolean isLogic(ModelElement me) {
        return StereotypeHelper.hasStereotye(me, RESTStereotypes.LOGIC.getName());
    }

    private boolean isRepository(ModelElement me) {
        return hasStereotype(RESTStereotypes.REPOSITORY.getName(), me);
    }

    private boolean isEntity(ModelElement me) {
        return hasStereotype(RESTStereotypes.ENTITY.getName(), me);
    }

    private boolean isDDL(ModelElement me) {
        return hasStereotype(RESTStereotypes.DDL.getName(), me);
    }

    private boolean isTSType(ModelElement me) {
        return hasStereotype(RESTStereotypes.TSTYPE.getName(), me);
    }

    private boolean hasStereotype(String sTypeName, ModelElement me) {
        return StereotypeHelper.hasStereotye(me, sTypeName);
    }

}
