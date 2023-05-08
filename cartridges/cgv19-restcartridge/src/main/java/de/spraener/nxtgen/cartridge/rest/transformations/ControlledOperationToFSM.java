package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.*;

import static de.spraener.nxtgen.cartridge.rest.transformations.TransformationHelper.firstToUpperCaser;

public class ControlledOperationToFSM implements Transformation {
    @Override
    public void doTransformation(ModelElement element) {
        if (!(element instanceof MClass)) {
            return;
        }
        MClass c = (MClass) element;
        if (c.getActivities() != null) {
            for (MActivity activity : c.getActivities()) {
                if (StereotypeHelper.hasStereotye(activity, RESTStereotypes.CONTROLLEDOPERATION.getName())) {
                    createFinalStateMachine(activity);
                }
            }
        }
    }

    private void createFinalStateMachine(MActivity activity) {
        FSMHelper fsmHelper = new FSMHelper(activity);
        MClass mClass = (MClass) activity.getParent();
        MPackage pkg = mClass.getPackage().findOrCreatePackage("logic");

        MClass fsmClass = pkg.createMClass(mClass.getName() + firstToUpperCaser(activity.getName()));
        fsmClass.putObject("fsmHelper", fsmHelper);
        fsmClass.putObject("originalClass", mClass);
        fsmClass.putObject("activity", activity);

        fsmClass.addStereotypes(new StereotypeImpl(RESTStereotypes.ACTIVITYIMPL.getName()));
        fsmHelper.setFSMClass(fsmClass);
        Stereotype sType = new StereotypeImpl(RESTStereotypes.CONTROLLEDOPERATION.getName());
        fsmClass.addStereotypes(sType);

        MOperation initOP = fsmClass.createOperation("initContext");
        initOP.createParameter("context", "FSMContext<" + mClass.getName() + ">");
        for (MActivityAction action : activity.getActions()) {
            addOperation(fsmHelper, action);
        }
    }

    private void addOperation(FSMHelper fsmHelper, MActivityAction action) {
        MClass fsmClass = fsmHelper.getFSMClass();
        MClass mClass = (MClass) action.getParent().getParent();
        if (fsmHelper.isInteractive(action)) {
            MOperation opBefore = fsmClass.createOperation(action.getName());
            opBefore.setType("Void");
            opBefore.createParameter("context", "FSMContext<" + mClass.getName() + ">");
            opBefore.putObject("activityAction", action);
            opBefore.setProperty("interruptedBefore", "true");
            for (Stereotype st : action.getStereotypes()) {
                opBefore.getStereotypes().add(st);
            }

            MOperation opReturnFrom = fsmClass.createOperation( "returnFrom"+ JavaHelper.firstToUpperCase(action.getName()) );
            opReturnFrom.setType("Object");
            opReturnFrom.createParameter("returnValue", "Object");
            opReturnFrom.createParameter("context", "FSMContext<" + mClass.getName() + ">");
            opReturnFrom.putObject("activityAction", action);
            opReturnFrom.setProperty("interruptedReturn", "true");
            for (Stereotype st : action.getStereotypes()) {
                opReturnFrom.getStereotypes().add(st);
            }
        } else {
            MOperation op = fsmClass.createOperation(action.getName());
            op.setType("Object");
            op.setName(action.getName());
            op.createParameter("context", "FSMContext<" + mClass.getName() + ">");
            op.putObject("activityAction", action);
            for (Stereotype st : action.getStereotypes()) {
                op.getStereotypes().add(st);
            }
        }

    }
}
