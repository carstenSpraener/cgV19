package de.spraener.nxtgen.cartridge.rest.cntrl;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTCartridge;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogicGeneratedTest {

    @Test
    void testLogicGenerated() {
        OOModel m = new OOModel();
        MPackage p = new MPackage();
        m.addModelElement(p);
        p.setName("pkg");
        p.setModel(m);
        MClass c = p.createMClass("ARessource");
        c.addStereotypes(new StereotypeImpl(RESTStereotypes.RESSOURCE.getName()));
        c.setModel(m);

        RESTCartridge cartridge = new RESTCartridge();
        for(Transformation t : cartridge.getTransformations()) {
            for(ModelElement me : m.getModelElements() ) {
                t.doTransformation(me);
            }
        }

        MClass logic = m.findClassByName("pkg.logic.ARessourceLogic");
        assertNotNull(logic );
        assertTrue(StereotypeHelper.hasStereotype(logic, RESTStereotypes.IMPL.getName()));

        MClass logicBase = m.findClassByName("pkg.logic.ARessourceLogicBase");
        assertNotNull(logic );
        assertTrue(StereotypeHelper.hasStereotype(logicBase, RESTStereotypes.LOGIC.getName()));
    }
}
