package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.ModelHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelationImplTest {

    /*
     * @startuml
     *  package ContainerA {
     *    object ElementA
     *  }
     *  package ContainerB {
     *    object ElementB
     *  }
     *  ElementA -- ElementB
     * @enduml
     */
    @Test
    public void testRelationImpl() throws Exception {
        ModelImpl m = new ModelImpl();
        ModelElementImpl pkgA = new ModelElementImpl();
        pkgA.setName("ContainerA");
        pkgA.setModel(m);
        pkgA.setXmiID("xmiid1");
        m.addModelElement(pkgA);

        ModelElementImpl pkgB = new ModelElementImpl();
        pkgB.setName("ContainerB");
        pkgB.setModel(m);
        pkgB.setXmiID("xmiid2");
        m.addModelElement(pkgB);

        ModelElementImpl eAInA = new ModelElementImpl();
        eAInA.setName("ElementA");
        eAInA.setModel(m);
        eAInA.setXmiID("xmiid3");
        pkgA.getChilds().add(eAInA);
        eAInA.setParent(pkgA);

        ModelElementImpl eBInB = new ModelElementImpl();
        eBInB.setName("ElementB");
        eBInB.setModel(m);
        eBInB.setXmiID("xmiid4");
        pkgB.getChilds().add(eBInB);
        eBInB.setParent(pkgB);

        RelationImpl rel = new RelationImpl();

        rel.setType("SomeRelation");
        rel.setTargetType("ContainerB.ElementB");
        rel.setTargetXmiID("xmiid4");
        eAInA.addRelations(rel);

        assertEquals("ContainerA.ElementA", ModelHelper.getFQName(eAInA, "."));
        assertEquals("ContainerB.ElementB",
                ModelHelper.getFQName(
                        ModelHelper.findByFQName(m, rel.getTargetType(), "."),
                        "."
                )
        );
    }
}
