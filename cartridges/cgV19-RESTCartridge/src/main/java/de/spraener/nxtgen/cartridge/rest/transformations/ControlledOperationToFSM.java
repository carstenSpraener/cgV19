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
        MPackage pkg = mClass.getPackage().findOrCreatePackage("logic");
        MClass fsmClass = pkg.createMClass(mClass.getName() + firstToUpperCaser(activity.getName()));
        fsmClass.addStereotypes(new StereotypeImpl(RESTStereotypes.IMPL.getName()));
        fsmHelper.setFSMClass(fsmClass);
        Stereotype sType = new StereotypeImpl(RESTStereotypes.CONTROLLEDOPERATION.getName());
        fsmClass.addStereotypes(sType);
        MOperation initOP = fsmClass.createOperation("initContext");
        initOP.createParameter("context", "FSMContext<"+mClass.getName()+">");
        for (MActivityAction action : activity.getActions()) {
            addOperation(fsmHelper, action);
        }
        addFSMBeforeAnnotation(initOP);
        setFSMClassAnnotation(fsmHelper, fsmClass, activity);

        String importList = fsmClass.getProperty("importList");
        if( null==importList ) {
            importList = "";
        }
        importList+="\nimport de.csp.fsm.FSMTransition;\n" +
                "import de.csp.fsm.FSMTransitions;\n" +
                "import de.csp.fsm.FSMRunnable;\n" +
                "import de.csp.fsm.FSMBefore;\n" +
                "import de.csp.fsm.FSMContext;\n\n" +
                "import "+mClass.getPackage().getFQName()+".model.*;";
        fsmClass.setProperty("importList", importList);
    }

    private void addFSMBeforeAnnotation(MOperation initOP) {
        String annitations = initOP.getProperty("annotations");
        if( annitations == null ) {
            annitations = "";
        }
        annitations+="@FSMBefore\n    ";
        initOP.setProperty("annotations", annitations);
    }

    private void setFSMClassAnnotation(FSMHelper fsmHelper, MClass fsmClass, MActivity activity) {
        StringBuilder annotations = new StringBuilder();
        annotations.append("@FSMRunnable(\n");
        annotations.append("    initialMethod = \"").append(fsmHelper.findInitNode(activity)).append("\",\n");
        annotations.append("    finalStates = {");
        boolean firstState = true;
        for( String state : fsmHelper.getFinalStates(activity) ) {
            if( !firstState ) {
                annotations.append(",");
            } else {
                firstState = false;
            }
            annotations.append("\n");
            annotations.append("        \"").append(state).append("\"");
        }
        annotations.append("\n    }\n");
        annotations.append(")\n");
        String fsmAnnos = fsmClass.getProperty("annotations");
        if( fsmAnnos == null ) {
            fsmAnnos = "";
        }
        fsmAnnos += "\n"+annotations.toString();
        fsmClass.setProperty("annotations", fsmAnnos);
    }

    private void addOperation(FSMHelper fsmHelper, MActivityAction action) {
        MClass fsmClass = fsmHelper.getFSMClass();
        MClass mClass = (MClass) action.getParent().getParent();
        MOperation op = fsmClass.createOperation(action.getName());
        op.setType("Object");
        op.setName(action.getName());
        op.createParameter("context", "FSMContext<"+mClass.getName()+">");
        StringBuffer annotations = new StringBuffer();
        for (ModelElement outgoing : fsmHelper.findOutgoings(action)) {
            String targetName = outgoing.getProperty("target");
            String guard = outgoing.getProperty("transitOn");
            if( guard == null ) {
                guard = outgoing.getProperty("guard");
            }
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
            String annotationsStr = "@FSMTransitions(transistions={"+annotations.toString()+"\n    })\n    ";
            op.setProperty("annotations", annotationsStr);
        }
    }
}
