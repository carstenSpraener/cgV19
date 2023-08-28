package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.cartridge.rest.transformations.ResourceToEntity;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MClass;

public class PhpEnsureEntityDefinitions extends PhpEnsureEntityDefinitionsBase {

    @Override
    public void doTransformationIntern(MClass me) {
        Model model = me.getModel();
        ResourceToEntity.ensureEntityDefinition(me);
        for (MAssociation association : me.getAssociations()) {
            if (opositeUndefined(association) && "OneToMany".equals(association.getAssociationType())) {
                // Create an association for back reference needed by Doctrine
                String attrName = me.getName().toLowerCase();
                association.setOpositeAttribute(attrName);

                MClass target = (MClass) ModelHelper.findByFQName(model, association.getType(), ".");
                MAssociation backRef = OOModelBuilder.createAssociation(target, me, attrName, "0..1",
                        a -> a.setProperty("direction", "from"),
                        a -> a.setAssociationType("composite"),
                        a -> a.setAssociationType("ManyToOne")
                );
            }
        }
    }

    public static boolean opositeUndefined(MAssociation association) {
        String opositeAttr = (""+association.getOpositeAttribute()).trim();
        return "".equals(opositeAttr) || "null".equals(opositeAttr);
    }
}
