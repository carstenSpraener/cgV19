package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

public class RemoveModelRootPackage extends RemoveModelRootPackageBase {
    @Override
    public void doTransformationIntern(MPackage modelRootPackage) {
        ModelElement parent = modelRootPackage.getParent();
        for (ModelElement child : modelRootPackage.getChilds()) {
            if (parent == null || !(parent instanceof MPackage) ) {
                ((OOModel) modelRootPackage.getModel()).getChilds().add(child);
                ((ModelElementImpl) child).setParent(null);
            } else {
                ((ModelElementImpl) child).setParent(parent);
            }
        }
    }
}
