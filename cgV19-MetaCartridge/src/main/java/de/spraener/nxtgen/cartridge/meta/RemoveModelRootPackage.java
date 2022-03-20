package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

public class RemoveModelRootPackage implements Transformation {
    @Override
    public void doTransformation(ModelElement element) {
        if( element instanceof MPackage && StereotypeHelper.hasStereotye(element, MetaCartridge.STEREOTYPE_MODEL_ROOT)) {
            MPackage modelRootPackage = (MPackage) element;
            ModelElement parent = modelRootPackage.getParent();
            for( ModelElement child : modelRootPackage.getChilds() ) {
                if( parent == null ) {
                    ((OOModel)element.getModel()).getChilds().add(child);
                    ((ModelElementImpl)child).setParent(null);
                } else {
                    ((ModelElementImpl)child).setParent(parent);
                }
            }
        }
    }
}
