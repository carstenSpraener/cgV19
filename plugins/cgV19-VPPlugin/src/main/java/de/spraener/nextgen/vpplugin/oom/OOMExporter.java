package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import de.spraener.nextgen.vpplugin.CgV19Plugin;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

import static de.spraener.nextgen.vpplugin.oom.PropertiesExporter.exportProperties;

public class OOMExporter implements Runnable {
    IModelElement root = null;
    String exportedModel;
    String rootPackageName;

    private static Map<String, Supplier<Exporter>> exporterRegistry = new HashMap<>();

    static {
        exporterRegistry.put(IModelElementFactory.MODEL_TYPE_STEREOTYPE, () -> new StereotypeExporter());
        ExporterRegistryFiller.fillRegistry(exporterRegistry);
    }

    private OOMExporter(String rootPackageName, IModelElement element) {
        this.root = (IModelElement) element;
        this.rootPackageName = rootPackageName;
    }

    public static Exporter findExporterByModelType(String modelType) {
        Supplier<Exporter> exporterSupplier = exporterRegistry.get(modelType);
        if (exporterSupplier == null) {
            return null;
        }
        return exporterSupplier.get();
    }

    public static String exportByPackageName(String rootPackage) {
        try {
            CgV19Plugin.log("OOM-Exporter called with rootPackage: '" + rootPackage+"'");
            IModelElement rootNode = findElementByPackageName(rootPackage);
            if (rootNode != null) {
                OOMExporter export = new OOMExporter(rootPackage, rootNode);
                export.run();
                return export.exportedModel;
            }
        } catch (Throwable e) {
            CgV19Plugin.log(e);
        }
        throw new IllegalArgumentException(String.format("Can not export model '%s'.", rootPackage));
    }

    private static IModelElement findElementByPackageName(String rootPackageName) {
        IProject prj = ApplicationManager.instance().getProjectManager().getProject();
        for (IModelElement mElement : prj.toModelElementArray()) {
            if (IModelElementFactory.MODEL_TYPE_PACKAGE.equals(mElement.getModelType())) {
                IPackage pkg = (IPackage) mElement;
                CgV19Plugin.log("Child mit Namen '" + mElement.getName() + "' ist ein IPackage.");
                if (pkg.getName().equals(rootPackageName)) {
                    CgV19Plugin.log("Rootpackage '" + mElement.getName() + "' gefunden.");
                    return pkg;
                } else if( rootPackageName.startsWith(getFQName(pkg)) ) {
                    IPackage subPackage = findSubPackageByName(pkg, rootPackageName);
                    if( subPackage != null ) {
                        return pkg;
                    }
                }
            } else if (IModelElementFactory.MODEL_TYPE_MODEL.equals(mElement.getModelType())) {
                CgV19Plugin.log("Child mit Namen '" + mElement.getName() + "' ist ein IModel.");
                if (mElement.getName().equals(rootPackageName)) {
                    return mElement;
                }
            } else if (IModelElementFactory.MODEL_TYPE_PROFILE.equals(mElement.getModelType())) {
                CgV19Plugin.log("Child mit Namen '" + mElement.getName() + "' ist ein IProfile.");
                if (mElement.getName().equals(rootPackageName)) {
                    return mElement;
                }
            } else {
                CgV19Plugin.log("Child mit Namen '" + mElement.getName() + "' hat unbekannten ModelType " + mElement.getModelType() + ".");
            }
        }
        return null;
    }

    private static IPackage findSubPackageByName(IPackage pkg, String rootPackageName) {
        if( getFQName(pkg).equals(rootPackageName) ) {
            return pkg;
        } else {
            for( IModelElement child : pkg.toChildArray() ) {
                if( child instanceof IPackage ) {
                    String childFQName = getFQName(child);
                    if( rootPackageName.startsWith(childFQName) ) {
                        return findSubPackageByName((IPackage)child, rootPackageName);
                    }
                }
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

    public static Collection<? extends IModelElement> findChildsByMetaType(IModelElement element, String modelType) {
        List<IModelElement> childs = new ArrayList<>();
        for (IModelElement child : element.toChildArray()) {
            if (child.getModelType().equals(modelType)) {
                childs.add(child);
            }
            if (child.childCount() > 0) {
                childs.addAll(findChildsByMetaType(child, modelType));
            }
        }
        return childs;
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
        pw.printf("%s  mPackage {\n", identation);
        // Hack because in VP you can not assign a stereotype to a profile.
        if( root.getModelType().equals(IModelElementFactory.MODEL_TYPE_PROFILE) && root.getDocumentation().contains("<<ModelRoot>>")) {
            pw.printf("%s    stereotype 'ModelRoot'\n", identation);
        }
        PropertiesExporter.exportProperties(
                pw,identation+"    ",
                root,
                PropertyOverwriter.overwrite("name", rootPackageName)
        );
        exportChilds(pw, identation + "    ", root);
        pw.printf("%s  }\n", identation);
        pw.println(identation + "}");
    }

    private void exportChilds(PrintWriter pw, String indentation, IModelElement mElement) {
        for (IModelElement child : mElement.toChildArray()) {
            exportElement(pw, indentation, child);
        }
    }

    public void exportElement(PrintWriter pw, String indentation, IModelElement element) {
        Exporter exporter = findExporterByModelType(element.getModelType());
        if (exporter != null) {
            exporter.export(this, pw, indentation, element);
            return;
        }
        pw.printf("%smElement {\n", indentation);
        exportProperties(pw, indentation + "  ", element);
        exportChilds(pw, indentation + "  ", element);
        pw.printf("%s}\n", indentation);
    }

    public static String getFQName(IModelElement modelElement) {
        if (modelElement.getParent() != null) {
            return getFQName(modelElement.getParent()) + "." + modelElement.getName();
        } else {
            return modelElement.getName();
        }
    }
}
