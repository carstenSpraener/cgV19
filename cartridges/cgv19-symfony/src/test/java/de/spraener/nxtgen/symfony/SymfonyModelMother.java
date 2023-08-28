package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

public class SymfonyModelMother {
    public static final String APP_PKG = "test.model.app";

    /**
     * @startuml
     * Package test.model.app {
     *   Package Entity {
     *     Class Test << Entity >>  {
     *      title: String
     *      group: boolean
     *    }
     *
     *     Class Result << Entity >>  {
     *         description: String
     *         isOK: boolean
     *     }
     *
     *     Test "1"*--->"0..* results" Result
     *     Test "1"*--->"1 lastResult" Result
     *   }
     *   Package Controller   {
     *      Class TestController << RESTController >> {
     *
     *      }
     *      TestController ..>Test
     *   }
     * }
     * @enduml
     *
     * @return
     */
    public static OOModel createDefaultModel() {
        OOModel model = OOModelBuilder.createModel(
                m -> OOModelBuilder.createPackage(m, "test.model",
                        p-> OOModelBuilder.createPackage(p, "app",
                                appPkg-> appPkg.getStereotypes().add(new StereotypeImpl(SymfonyStereotypes.SYMFONYAPP.getName())),
                                SymfonyModelMother::createAppModel
                        )
                )
        );

        return model;
    }

    public static void createAppModel(MPackage p) {
        OOModelBuilder.createPackage(p, "entity",
                cp -> OOModelBuilder.createMClass(cp, "Test",
                        c -> c.getStereotypes().add(new StereotypeImpl(RESTStereotypes.ENTITY.getName())),
                        c -> c.createAttribute("title", "String"),
                        c -> c.createAttribute("group", "String")
                )
        );
        final MClass entity = ((OOModel)p.getModel()).findClassByName("test.model.app.entity.Test");
        OOModelBuilder.createPackage(p, "entity",
                cp -> OOModelBuilder.createMClass(cp, "Result",
                        c -> c.getStereotypes().add(new StereotypeImpl(RESTStereotypes.ENTITY.getName())),
                        c -> c.createAttribute("description", "String"),
                        c -> c.createAttribute("isOK", "boolean"),
                        c -> OOModelBuilder.createAssociation(entity, c, "results", "0..*"),
                        c -> OOModelBuilder.createAssociation(entity, c, "lastResult", "0..1")
                )
        );
        OOModelBuilder.createPackage(p, "controller",
                cp -> OOModelBuilder.createMClass(cp, "TestController",
                        c -> c.getStereotypes().add(new StereotypeImpl(RESTStereotypes.RESTCONTROLLER.getName())),
                        c -> OOModelBuilder.createDependency(c, entity,
                                dep -> OOModelBuilder.addStereotype(dep, RESTStereotypes.CONTROLLEDENTITY.getName())
                        )
                )
        );

    }

    public static MClass getTestController(OOModel model) {
        return model.findClassByName(SymfonyModelMother.APP_PKG+".controller.TestController");
    }

    public static MClass getTestEntity(OOModel model) {
        return model.findClassByName(SymfonyModelMother.APP_PKG+".entity.Test");
    }
}
