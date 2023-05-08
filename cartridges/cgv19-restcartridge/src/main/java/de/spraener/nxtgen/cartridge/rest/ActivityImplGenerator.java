package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.cartridge.rest.transformations.FSMHelper;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.model.MActivity;
import de.spraener.nxtgen.oom.model.MActivityAction;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MOperation;

public class ActivityImplGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass fsmClass = (MClass)element;
        FSMHelper fsmHelper = (FSMHelper)fsmClass.getObject("fsmHelper");
        MClass mClass = (MClass)fsmClass.getObject("originalClass");

        String importList = fsmClass.getProperty("importList");
        if( null==importList ) {
            importList = "";
        }
        importList+="\nimport de.csp.fsm.FSMTransition;\n" +
                "import de.csp.fsm.FSMTransitions;\n" +
                "import de.csp.fsm.FSMRunnable;\n" +
                "import de.csp.fsm.FSMBefore;\n" +
                "import de.csp.fsm.FSMPrepareFor;\n" +
                "import de.csp.fsm.FSMReturnFrom;\n" +
                "import de.csp.fsm.FSMContext;\n\n" +
                "import "+mClass.getPackage().getFQName()+".model.*;";
        fsmClass.setProperty("importList", importList);
        setFSMClassAnnotation(fsmHelper, fsmClass);

        for( MOperation op : fsmClass.getOperations() ) {
            if( op.getName().equals("initContext")) {
                addFSMBeforeAnnotation(op);
            } else {
                addAnnotations(fsmHelper, op);
            }
        }

        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java", fsmClass.getPackage().getFQName(), fsmClass.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("PoJoGenerator", fsmClass, "/PoJoTemplate.groovy");
        jcb.addCodeBlock(gcb);
        return jcb;
    }

    private void setFSMClassAnnotation(FSMHelper fsmHelper, MClass fsmClass) {
        MActivity activity = (MActivity)fsmClass.getObject("activity");
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

    private void addFSMBeforeAnnotation(MOperation initOP) {
        String annitations = initOP.getProperty("annotations");
        if( annitations == null ) {
            annitations = "";
        }
        annitations+="@FSMBefore\n    ";
        initOP.setProperty("annotations", annitations);
    }

    private void addAnnotations(FSMHelper fsmHelper, MOperation op) {
        MActivityAction action = (MActivityAction)op.getObject("activityAction");
        StringBuffer annotations = new StringBuffer();
        String annotationsStr = "";
        if( op.getProperty("interruptedBefore") == null) {
            for (ModelElement outgoing : fsmHelper.findOutgoings(action)) {
                String targetName = outgoing.getProperty("target");
                String guard = outgoing.getProperty("transitOn");
                if (guard == null) {
                    guard = outgoing.getProperty("guard");
                }
                if (guard == null) {
                    guard = "VOID";
                }
                ModelElementImpl transition = new ModelElementImpl();
                transition.setParent(op);
                transition.setMetaType("mTransition");
                op.getChilds().add(transition);
                annotations.append("\n        @FSMTransition(target=\"" + targetName + "\", guard=\"" + guard + "\"),");
                if( annotations.length()> 0 ) {
                     annotationsStr = "@FSMTransitions(transistions={" + annotations.toString() + "\n    })\n    ";
                }
            }
        } else if(op.getProperty("interruptedBefore") != null ) {
            annotationsStr = "@FSMPrepareFor(\""+action.getName()+"\")\n    ";
        }
        if(op.getProperty("interruptedReturn") != null ) {
            annotationsStr = "@FSMReturnFrom(\""+action.getName()+"\")\n    "+annotationsStr;
        }
        op.setProperty("annotations", annotationsStr);
    }

}
