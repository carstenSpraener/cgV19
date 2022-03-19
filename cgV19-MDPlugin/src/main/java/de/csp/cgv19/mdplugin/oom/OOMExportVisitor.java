package de.csp.cgv19.mdplugin.oom;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.symbols.shapes.SlotsHelper;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.jmi.helpers.ValueSpecificationHelper;
import com.nomagic.uml2.ext.jmi.reflect.VisitorContext;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityEdge;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityFinalNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ControlFlow;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.InitialNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.activities.mdintermediateactivities.DecisionNode;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.Interface;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.impl.ModelHierarchyVisitor;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.util.function.Function;

import static de.csp.cgv19.mdplugin.oom.OOMExportSupport.*;

public class OOMExportVisitor extends ModelHierarchyVisitor {
    private PrintWriter pw;
    private String prefix = "        ";

    OOMExportVisitor(PrintWriter pw) {
        this.pw = pw;
    }

    private void log(String msg) {
        Application.getInstance().getGUILog().log(msg);
    }

    private void visitOwnedElements(Element e, Function<Element, Boolean> filter) {
        prefix = prefix + "  ";
        for (Element child : e.getOwnedElement()) {
            try {
                if (StereotypesHelper.hasStereotype(child, "Manifestiert")) {
                    continue;
                }
                if (filter == null || filter.apply(child)) {
                    //Application.getInstance().getGUILog().log("visit child >>"+child.getHumanName()+"<< of Element "+e.getHumanName());
                    child.accept(this);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        prefix = prefix.substring(0, prefix.length() - 2);
    }

    private void visitOwnedElements(Element e) {
        this.visitOwnedElements(e, null);
    }

    private void println(String txt) {
        txt = txt.replaceAll("\n", "\n" + prefix);
        pw.println(prefix + txt);
    }


    @Override
    public void visitClass(Class clazz, VisitorContext context) {
        // Application.getInstance().getGUILog().log("Visiting class "+clazz.getHumanName());
        println("mClass {");
        println("  name '" + clazz.getName() + "'");
        println("  xmiID '" + clazz.getID() + "'");
        OOMExportSupport.printStandardAttributes(clazz, this::println);
        if (clazz.isAbstract()) {
            println("  isAbstract true");
        }
        printStereotypes("    ", clazz);
        for (Interface interf : ModelHelper.getRealizedInterfaces(clazz)) {
            println("  relation {");
            println("    type 'implements'");
            println("    targetXmID '" + interf.getID() + "'");
            println("    targetType '" + toTypeName(interf.getQualifiedName()) + "'");
            println("  }");
        }
        for (Classifier superClass : ModelHelper.getGeneralClassifiers(clazz)) {
            println("  relation {");
            println("    type 'extends'");
            println("    targetXmID '" + superClass.getID() + "'");
            println("    targetType '" + toTypeName(superClass.getQualifiedName()) + "'");
            println("  }");

        }
        visitOwnedElements(clazz);
        println("}");
    }

    @Override
    public void visitPackage(Package pkg, VisitorContext context) {
        // log("Visiting package "+pkg.getHumanName());
        println("mPackage {\n" +
                "  name '" + pkg.getName() + "'");
        OOMExportSupport.printStandardAttributes(pkg, this::println);
        visitOwnedElements(pkg, e -> e instanceof Package || e instanceof Class);
        println("}");
    }

    @Override
    public void visitProperty(Property property, VisitorContext context) {
        // log("Visiting property "+property.getHumanName());

        if (property.getAssociation() != null) {
            Association assoc = property.getAssociation();
            printAssociation(property, assoc);
        } else {
            println("mAttribute {");
            println("    name '" + property.getName() + "'");
            OOMExportSupport.printStandardAttributes(property, this::println);
            printStereotypes("    ", property);
            Type type = property.getType();
            if( type != null ) {
                println("    type '" + toTypeName(type.getQualifiedName()) + "'");
            }
            println("    visibility '" + property.getVisibility() + "'");
            if (property.getAssociation() != null) {
                String multiplicity = ModelHelper.getMultiplicity(property);
                println("    multiplicity '" + multiplicity + "'");
            }
            println("}");
        }
        visitOwnedElements(property);
    }

    private void printAssociation(Property property, Association assoc) {
        println("mAssociation {");
        printStereotypes("        ", property.getAssociation());
        println("    assocId '"+assoc.getID()+"'");
        Property oposite =  property.getOpposite();
        if( oposite!=null ) {
            println( "    opositeAttribute '"+oposite.getName()+"'");
        }
        String opositeMultiplicity = ModelHelper.getMultiplicity(oposite);
        println("    opositeMultiplicity '"+opositeMultiplicity+"'");
        String associationType = "";
        if( opositeMultiplicity.equals("1") ) {
            associationType = "OneTo";
        } else {
            associationType = "ManyTo";
        }
        String multiplicity = ModelHelper.getMultiplicity(property);
        if( multiplicity.equals("1") ) {
            associationType += "One";
        } else {
            associationType += "Many";
        }
        println("    associationType '"+associationType+"'");
        println("    type '"+toTypeName(property.getType().getQualifiedName())+"'");
        println("    name '"+property.getName()+"'");
        println("    multiplicity '" + multiplicity + "'");
        println("    composite '"+property.isComposite()+"'" );
        println("}");
    }

    private void printStereotypes(String prefix, Element e) {
        for (Stereotype stType : StereotypesHelper.getStereotypes(e)) {
            println(prefix + "stereotype '" + stType.getName() + "', {");
            for (Slot taggedValue : StereotypesHelper.collectOwnedSlots(e)) {
                ValueSpecification vSpec = taggedValue.getValue().get(0);
                String tvName = getSlotName(taggedValue);
                String tvValue = getStringValue(taggedValue.getValue());
                println(prefix + "    taggedValue '" + tvName + "', '" + tvValue + "'");
            }
            println(prefix + "}");
        }
    }

    @Override
    public void visitAssociation(Association assoc, VisitorContext context) {
        //log("Visiting Association "+assoc.getHumanName());
        Property p = assoc.getMemberEnd().get(0);
        String multiplicity = ModelHelper.getMultiplicity(p);
        println("mReference {");
        OOMExportSupport.printStandardAttributes(assoc, this::println);
        printStereotypes("    ", p);
        println("    name '" + p.getName() + "'");
        println("    multiplicity '" + multiplicity + "'");
        println("}");
        visitOwnedElements(assoc);
    }

    @Override
    public void visitDependency(Dependency dependency, VisitorContext context) {
        //log("Visiting dependency "+dependency.getHumanName());
        visitOwnedElements(dependency);
    }

    @Override
    public void visitOperation(Operation op, VisitorContext context) {
        println("mOperation {");
        println("    name '" + op.getName() + "'");
        println("    type '" + toTypeName(op.getType().getQualifiedName()) + "'");
        OOMExportSupport.printStandardAttributes(op, this::println);
        printStereotypes("    ", op);
        for (Parameter p : op.getOwnedParameter()) {
            visitParameter(p, context);
        }
        println("}");
    }

    @Override
    public void visitParameter(Parameter p, VisitorContext context) {
        if (StringUtils.isEmpty(p.getName())) {
            return;
        }
        println("        mParameter {");
        println("            name '" + p.getName() + "'");
        println("            type '" + toTypeName(p.getType().getQualifiedName()) + "'");
        printStereotypes("            ", p);
        OOMExportSupport.printStandardAttributes(p, this::println);
        println("        }");
    }

    @Override
    public void visitActivity(Activity activity, VisitorContext context) {
        println("mActivity {");
        printStereotypes("  ", activity);
        println("  id '" + activity.getID() + "'");
        println("  name '" + activity.getName()+ "'");
        OOMExportSupport.printStandardAttributes(activity, this::println);
        //super.visitActivity(activity,context);
        visitOwnedElements(activity);
        println("}");
    }

    @Override
    public void visitAction(Action element, VisitorContext context) {
        println("mAction {");
        printStereotypes("  ", element);
        println("  id '" + element.getID() + "'");
        println("  name '" + element.getName() + "'");
        OOMExportSupport.printStandardAttributes(element, this::println);
        super.visitAction(element, context);
        println("}");
    }

    @Override
    public void visitControlFlow(ControlFlow cf, VisitorContext context) {
        println("mControlFlow {");
        printStereotypes("  ", cf);
        OOMExportSupport.printStandardAttributes(cf, this::println);
        println("   source '" + cf.getSource().getName() + "'");
        println("   sourceID '" + cf.getSource().getID() + "'");
        println("   target '" + cf.getTarget().getName() + "'");
        println("   targetID '" + cf.getTarget().getID() + "'");
        println("   name '" + cf.getName() + "'");
        println("   id '" + cf.getID() + "'");
        if (cf.getGuard() != null) {
            String guard = ValueSpecificationHelper.getValueString(cf.getGuard());
            println("   transitOn '" + guard + "'");
        }
        super.visitControlFlow(cf, context);
        println("}");
    }

    @Override
    public void visitDecisionNode(DecisionNode dn, VisitorContext context) {
        println("mDecision {");
        OOMExportSupport.printStandardAttributes(dn, this::println);
        printStereotypes("  ", dn);
        println("  id '" + dn.getID() + "'");
        println("  incoming {");
        for (ActivityEdge e : dn.getIncoming()) {
            printActivityEdge(e);
        }
        println("  }");
        println("  outgoing {");
        for (ActivityEdge e : dn.getOutgoing()) {
            printActivityEdge(e);
        }
        println("  }");
        super.visitDecisionNode(dn, context);
        println("}");
    }

    private void printActivityEdge(ActivityEdge e) {
        println("    mControlFlow { ");
        printStereotypes("  ", e);
        println("      source '" + e.getSource().getName() + "'");
        println("      sourceID '" + e.getSource().getID() + "'");
        println("      target '" + e.getTarget().getName() + "'");
        println("      targetID '" + e.getTarget().getID() + "'");
        println("      name '" + e.getName() + "'");
        println("      id '" + e.getID() + "'");
        String guard = ValueSpecificationHelper.getValueString(e.getGuard());
        println("      transitOn '" + guard + "'");
        println("    }");
    }

    @Override
    public void visitInitialNode(InitialNode element, VisitorContext context) {
        println("initNode {");
        OOMExportSupport.printStandardAttributes(element, this::println);
        printStereotypes("  ", element);
        println("  id: '" + element.getID() + "'");
        super.visitInitialNode(element, context);
        println("}");
    }

    @Override
    public void visitActivityFinalNode(ActivityFinalNode element, VisitorContext context) {
        println("finalNode {");
        OOMExportSupport.printStandardAttributes(element, this::println);
        printStereotypes("  ", element);
        println("  id: '" + element.getID() + "'");
        println("  value: '" + element.getName() + "'");
        super.visitActivityFinalNode(element, context);
        println("}");
    }
}

