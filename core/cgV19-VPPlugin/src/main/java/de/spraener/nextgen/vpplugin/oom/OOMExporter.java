package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import de.spraener.nextgen.vpplugin.CgV19Plugin;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import static de.spraener.nextgen.vpplugin.oom.PropertiesExporter.*;

public class OOMExporter implements Runnable {
    IModelElement root = null;
    String exportedModel;
    String rootPackageName;

    private static Map<String, Supplier<Exporter>> exporterRegistry = new HashMap<>();

    static {
        exporterRegistry.put(IModelElementFactory.MODEL_TYPE_STEREOTYPE, ()->new StereotypeExporter());
    }

    private OOMExporter(String rootPackageName, IModelElement element) {
        this.root = (IModelElement) element;
        this.rootPackageName = rootPackageName;
    }

    public static Exporter findExporterByModelType(String modelType) {
        Supplier<Exporter> exporterSupplier = exporterRegistry.get(modelType);
        if( exporterSupplier == null ) {
            return null;
        }
        return exporterSupplier.get();
    }


    public static String exportByPackageName(String rootPackage) {
        try {
            CgV19Plugin.log("OOM-Exporter called with rootPackage: " + rootPackage);
            IModelElement rootNode = findElementByPackageName(rootPackage);
            if (rootNode != null) {
                OOMExporter export = new OOMExporter(rootPackage, rootNode);
                export.run();
                return export.exportedModel;
            }
        } catch (Exception e) {
            CgV19Plugin.log(e);
        }
        return "{\"message\":\"Rootpackage '" + rootPackage + "' not found\"}";
    }

    private static IModelElement findElementByPackageName(String rootPackage) {
        IProject prj = ApplicationManager.instance().getProjectManager().getProject();
        for (IModelElement mElement : prj.toModelElementArray()) {
            if ( IModelElementFactory.MODEL_TYPE_PACKAGE.equals(mElement.getModelType()) ) {
                IPackage pkg = (IPackage) mElement;
                CgV19Plugin.log("Child mit Namen '" + mElement.getName() + "' ist ein IPackage.");
                if (pkg.getName().equals(rootPackage)) {
                    return pkg;
                }
            } else if ( IModelElementFactory.MODEL_TYPE_MODEL.equals(mElement.getModelType()) ) {
                CgV19Plugin.log("Child mit Namen '" + mElement.getName() + "' ist ein IModel.");
                if( mElement.getName().equals(rootPackage) ) {
                    return mElement;
                }
            } else if ( IModelElementFactory.MODEL_TYPE_PROFILE.equals(mElement.getModelType()) ) {
                CgV19Plugin.log("Child mit Namen '" + mElement.getName() + "' ist ein IProfile.");
                if( mElement.getName().equals(rootPackage) ) {
                    return mElement;
                }
            } else {
                CgV19Plugin.log("Child mit Namen '" + mElement.getName() + "' hat unbekannten ModelType "+mElement.getModelType()+".");
            }
        }
        return null;
    }

    private static IModelElement findPackage(IModel mElement, String pkgName) {
        CgV19Plugin.log("Suche nach Paket " + pkgName + " in Model " + mElement.getName() + ".");
        for (IModelElement mChild : mElement.toChildArray()) {
            if (IModelElementFactory.MODEL_TYPE_PACKAGE.equals(mChild.getModelType())) {
                CgV19Plugin.log("Model " + mElement.getName() + " enthält Paket mit Namen " + mChild.getName() + ".");
                if (mChild.getName().equals(pkgName)) {
                    CgV19Plugin.log("Paket mit Namen " + mChild.getName() + " gefunden!");
                    return mChild;
                }
            }
        }
        return null;
    }

    public void run() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        exportModel(pw, "", rootPackageName, root);
        pw.flush();
        this.exportedModel = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    private void exportModel(PrintWriter pw, String identation, String rootPackageName, IModelElement root) {
        pw.println("import de.spraener.nxtgen.groovy.ModelDSL\n");
        pw.println(identation + "ModelDSL.make {");
        if( root instanceof IPackage ) {
            exportPackage(pw, identation + "  ", (IPackage) root);
        } else {
            exportElement(pw, identation+"  ", root);
        }
        pw.println(identation + "}");
    }

    private void exportPackage(PrintWriter pw, String indentation, IPackage root) {
        pw.println(indentation + "mPackage {");
        exportProperties(pw, indentation + "  ", root);
        exportChilds(pw, indentation + "  ", root);
        pw.println(indentation + "}");
    }

    private void exportChilds(PrintWriter pw, String indentation, IModelElement mElement) {
        for (IModelElement child : mElement.toChildArray()) {
            if (IModelElementFactory.MODEL_TYPE_PACKAGE.equals(child.getModelType())) {
                exportPackage(pw, indentation, (IPackage) child);
            } else if (IModelElementFactory.MODEL_TYPE_CLASS.equals(child.getModelType())) {
                exportClass(pw, indentation, (IClass) child);
            } else if (IModelElementFactory.MODEL_TYPE_ATTRIBUTE.equals(child.getModelType())) {
                exportAttribute(pw, indentation, (IAttribute) child);
            } else {
                exportElement(pw, indentation, child);
            }
        }
    }

    private void exportAttribute(PrintWriter pw, String indentation, IAttribute attr) {
        pw.printf("%smAttribute {\n", indentation);
        exportProperties(pw, indentation + "  ", attr);
        pw.printf("%s  type '%s'\n", indentation, attr.getTypeAsString());
        if (!StringUtils.isEmpty(attr.getTypeModifier())) {
            pw.printf("%s  modifier '%s'\n", indentation, attr.getTypeModifier());
        }
        exportChilds(pw, indentation + "  ", attr);
        pw.printf("%s}\n", indentation);
    }

    private void exportElement(PrintWriter pw, String indentation, IModelElement element) {
        Exporter exporter = findExporterByModelType(element.getModelType());
        if( exporter != null ) {
            exporter.export(pw,indentation,element);
            return;
        }
        pw.printf("%smElement {\n", indentation);
        exportProperties(pw, indentation + "  ", element);
        exportChilds(pw, indentation + "  ", element);
        pw.printf("%s}\n", indentation);
    }

    private void exportClass(PrintWriter pw, String indentation, IClass clazz) {
        pw.printf("%smClass {\n", indentation);
        exportProperties(pw, indentation + "  ", clazz);
        exportChilds(pw, indentation + "  ", clazz);
        for (IRelationshipEnd rel : clazz.toToRelationshipEndArray()) {
            exportRelationshipEnd(pw, indentation + "  ", rel);
        }
        for (IRelationshipEnd rel : clazz.toFromRelationshipEndArray()) {
            exportRelationshipEnd(pw, indentation + "  ", rel);
        }
        for (IRelationship rel : clazz.toFromRelationshipArray()) {
            exportRelationship(pw, indentation + "  ", rel);
        }
        for (IOperation op : clazz.toOperationArray()) {
            exportOperation(pw, indentation + "  ", op);
        }
        pw.printf("%s}\n", indentation);
    }

    private void exportOperation(PrintWriter pw, String indentation, IOperation op) {
        pw.printf("%soperation {\n", indentation);
        pw.printf("%s  type '%s'\n", indentation, op.getReturnTypeAsString());
        exportProperties(pw, indentation + "  ", op);
        for (IParameter p : op.toParameterArray()) {
            exportParameter(pw, indentation + "  ", p);
        }
        pw.printf("%s}\n", indentation);
    }

    private void exportParameter(PrintWriter pw, String indentation, IParameter p) {
        pw.printf("%smParam {\n", indentation);
        pw.printf("%s  type '%s'\n", indentation, p.getTypeAsString());
        exportProperties(pw, indentation + " ", p);
        pw.printf("%s}\n", indentation);
    }

    private void exportRelationshipEnd(PrintWriter pw, String indent, IRelationshipEnd relEnd) {
        IRelationship rel = relEnd.getEndRelationship();
        if (rel instanceof IAssociation) {
            exportAssociation(pw, indent, (IAssociation) rel, relEnd.getOppositeEnd());
        } else {
            pw.printf("%smRelation {\n", indent);
            exportProperties(pw, indent + "  ", rel);
            pw.printf("%s}\n", indent);
        }
    }

    private void exportRelationship(PrintWriter pw, String indent, IRelationship rel) {
        pw.printf("%smRelation {\n", indent);
        exportProperties(pw, indent + "  ", rel);
        pw.printf("%s}\n", indent);
    }

    private void exportAssociation(PrintWriter pw, String indent, IAssociation assoc, IRelationshipEnd relEnd) {
        if (relEnd instanceof IAssociationEnd) {
            IAssociationEnd assocEnd = (IAssociationEnd) relEnd;
            if( assocEnd.getNavigable()==2 ) {
                return;
            }
            pw.printf("%smAssociation {\n", indent);
            exportProperties(pw, indent + "  ", assocEnd);
            pw.printf("%s  type '%s'\n", indent, assocEnd.getAggregationKind());
            pw.printf("%s  multiplicity '%s'\n", indent, assocEnd.getMultiplicity());
            pw.printf("%s  target '%s'\n", indent, getFQName(assocEnd.getModelElement()));
            pw.printf("%s  navigable '%d'\n", indent, assocEnd.getNavigable());
            pw.printf("%s}\n", indent);
        }
    }

    private String getFQName(IModelElement modelElement) {
        if( modelElement.getParent()!=null ) {
            return getFQName(modelElement.getParent())+"."+modelElement.getName();
        } else {
            return modelElement.getName();
        }
    }
}
