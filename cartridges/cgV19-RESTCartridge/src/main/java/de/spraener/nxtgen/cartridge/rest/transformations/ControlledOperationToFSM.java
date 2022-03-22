package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;

import static de.spraener.nxtgen.cartridge.rest.transformations.TransformationHelper.*;

public class ControlledOperationToFSM implements Transformation {
    @Override
    public void doTransformation(ModelElement element) {
        if (!(element instanceof MClass)) {
            return;
        }
        MClass c = (MClass) element;
        if( c.getActivities()!=null) {
            for (MActivity activity : c.getActivities()) {
                if( StereotypeHelper.hasStereotye(activity, RESTStereotypes.CONTROLLEDOPERATION.getName())) {
                    createFinalStateMachine(activity);
                }
            }
        }
    }

    private void createFinalStateMachine(MActivity activity) {
        FSMHelper fsmHelper = new FSMHelper(activity);
        MClass mClass = (MClass) activity.getParent();
        MPackage pkg = mClass.getPackage();
        MClass fsmClass = pkg.createMClass(mClass.getName() + firstToUpperCaser(activity.getName()));
        fsmClass.addStereotypes(new StereotypeImpl(RESTStereotypes.IMPL.getName()));
        fsmHelper.setFSMClass(fsmClass);
        Stereotype sType = new StereotypeImpl(RESTStereotypes.CONTROLLEDOPERATION.getName());
        fsmClass.addStereotypes(sType);
        for (MActivityAction action : activity.getActions()) {
            addOperation(fsmHelper, action);
        }
    }

    private void addOperation(FSMHelper fsmHelper, MActivityAction action) {
        MClass fsmClass = fsmHelper.getFSMClass();
        MOperation op = fsmClass.createOperation(action.getName());
        op.setName(action.getName());
        MParameter p = op.createParameter("context", "java.util.Map<String,String>");
        Stereotype sType = new StereotypeImpl(RESTStereotypes.CONTROLLEDOPERATIONNODE.getName());
        StringBuffer annotations = new StringBuffer();
        for (ModelElement outgoing : fsmHelper.findOutgoings(action)) {
            String targetName = outgoing.getProperty("target");
            String guard = outgoing.getProperty("transitOn");
            if( guard==null) {
                guard = "VOID";
            }
            ModelElementImpl transition = new ModelElementImpl();
            transition.setParent(op);
            transition.setMetaType("mTransition");
            op.getChilds().add(transition);
            annotations.append("\n        @FSMTransition(target=\""+targetName+"\", guard=\""+guard+"\"),");
        }
        if( annotations.length()> 0 ) {
            String annotatrionsStr = "@FSMTransitions({"+annotations.toString()+"\n    })\n    ";
            op.setProperty("annotations", annotatrionsStr);
        }
    }
}
