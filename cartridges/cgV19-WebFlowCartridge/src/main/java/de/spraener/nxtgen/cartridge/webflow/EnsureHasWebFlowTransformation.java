package de.spraener.nxtgen.cartridge.webflow;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.List;

public class EndureHasWebFlowTransformation extends EndureHasWebFlowTransformationBase {
    private boolean dummyDefined = false;
    @Override
    public void doTransformationIntern(ModelElement me) {
        if( dummyDefined ) {
            return;
        }
        ModelElement webFlow = ((OOModel)me.getModel()).getModelElements().stream()
                .filter( e -> (e instanceof MClass) )
                .map( e -> ((MClass)e))
                .filter(e -> StereotypeHelper.hasStereotye(e, WebFlowStereotypes.WEBFLOW.getName()))
                .findFirst()
                .orElse(null);
        if( webFlow==null ) {
            List<ModelElement> elementList = ((OOModel)me.getModel()).getModelElements();
            webFlow = elementList.stream()
                    .filter( e -> (e instanceof MActivity) )
                    .map( e -> ((MActivity)e))
                    .filter(e -> {
                        return StereotypeHelper.hasStereotye(e, WebFlowStereotypes.WEBFLOW.getName());
                    })
                    .findFirst()
                    .orElse(null);
        }
        if( webFlow == null ) {
            OOModel oom = (OOModel)me.getModel();
            MClass dummy = new MClass();
            dummy.setName("DummyFlow");
            StereotypeImpl st = new StereotypeImpl("WebFlow");
            dummy.addStereotypes(st);
            oom.addModelElement(dummy);
        }
        dummyDefined = true;
    }
}
