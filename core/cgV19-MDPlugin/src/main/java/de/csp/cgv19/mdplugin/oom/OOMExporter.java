package de.csp.cgv19.mdplugin.oom;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class OOMExporter implements Runnable {
    Element root = null;
    String exportedModel;
    String rootPackageName;

    private OOMExporter(String rootPackageName, BaseElement element) {
        this.root = (Element)element;
        this.rootPackageName = rootPackageName;
    }

    public static String exportByPackageName(String rootPackage) {
        Element rootNode = findElementByPackageName(rootPackage);
        OOMExporter export = new OOMExporter(rootPackage, rootNode);
        export.run();
        return export.exportedModel;
    }

    private static Element findElementByPackageName(String rootPackage) {
        Project prj = Application.getInstance().getProject();
        StringTokenizer strTok = new StringTokenizer(rootPackage, ".",false);
        Package parent = prj.getPrimaryModel();
        while( strTok.hasMoreTokens() ) {
            String childName = strTok.nextToken();
            for( Package child : parent.getNestedPackage() ) {
                if( child.getName().equals(childName)) {
                    parent = child;
                    continue;
                }
            }
        }
        return parent;
    }

    public void run() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println(
                "import de.spraener.nxtgen.groovy.ModelDSL\n" +
                "\n" +
                "ModelDSL.make {\n"
        );
        OOMExportVisitor visitor = new OOMExportVisitor(pw);
        ArrayList<Element> all = new ArrayList<>();
        all.add(root);
        try {
            root.accept(visitor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pw.println("}");
        pw.flush();
        pw.close();
        this.exportedModel = baos.toString();
    }

    public static void export(BaseElement element) {
        if( element instanceof Package ) {
            OOMExporter exporter = new OOMExporter(((Package) element).getQualifiedName(), element);
            exporter.run();
        } else {
            Application.getInstance().getGUILog().log("Nur Pakete können exportiert werden.", true);
        }
    }
}
