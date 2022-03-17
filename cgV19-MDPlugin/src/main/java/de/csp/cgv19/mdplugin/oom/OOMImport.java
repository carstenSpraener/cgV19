package de.csp.cgv19.mdplugin.oom;

import com.google.gson.Gson;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.javalin.http.Context;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

public class OOMImport {
    private static Gson gson = new Gson();
    OOMGroovyUpdater stereotypesUpdater = new OOMGroovyUpdater(this, "StereotypeUpdater.groovy");
    OMMPropertyUpdater oomPropertyUpdater = new OMMPropertyUpdater(this);

    private Project project = null;

    public static void importOOM(Context ctx) {
        Application.getInstance().getGUILog().writeLogText("Starte OO-Import", true);
        try {
            Model model = readModelFromPayload(ctx);
            if (model != null) {
                Application.getInstance().getGUILog().writeLogText("Received Model.", true);
                new OOMImport().importOOM(model);
            }
        } catch (Throwable t) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            t.printStackTrace(ps);
            ps.flush();
            Application.getInstance().getGUILog().writeLogText("Exception in import: " + baos.toString(), true);
        }
    }

    @Nullable
    private static Model readModelFromPayload(Context ctx) throws Exception {
        String type = ctx.header("XX-CGV19-Type");
        if (StringUtils.isEmpty(type)) {
            type = "oom";
            String contentType = ctx.header("Content-Type");
            if (contentType != null && contentType.toLowerCase().contains("json")) {
                type = "json";
            }
        }
        return readFromPayload(type, ctx.body());
    }

    public static Model readFromPayload(String type, String payload) throws Exception {
        Model model = null;
        switch (type) {
            case "oom":
                Application.getInstance().getGUILog().writeLogText("Reading OOM script from payload", true);
                model = parseGroovy(payload);
                break;
            case "json":
                Application.getInstance().getGUILog().writeLogText("Reading JSON from payload", true);
                model = new Gson().fromJson(payload, OOModel.class);
                break;
            case "bin":
                Application.getInstance().getGUILog().writeLogText("Reading Serialized Model from payload", true);
                model = desirializeFromBody(payload);
                break;
        }
        return model;
    }

    private static Model parseGroovy(String payload) {
        Application.getInstance().getGUILog().writeLogText("Sarting Groovy Parser", true);
        try {
            Binding b = new Binding();
            GroovyShell groovyShell = new GroovyShell(b);
            Model m = (Model) groovyShell.evaluate(payload);
            Application.getInstance().getGUILog().writeLogText("Parsed Model with " + m.getModelElements().size() + " root elements.", true);
            return m;
        } catch (Exception e) {
            Application.getInstance().getGUILog().writeLogText("Error in GroovyScript: " + e.getMessage(), true);
        }
        return null;
    }

    private static Model desirializeFromBody(String body) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getEncoder().encode(body.getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        return (Model) ois;
    }

    public OOMImport() {
        this.project = Application.getInstance().getProject();
    }

    public void importOOM(Model model) throws Exception {
        try {
            SessionManager.getInstance().createSession(project, "importing OOM-Model");
            for (ModelElement me : model.getModelElements()) {
                Application.getInstance().getGUILog().writeLogText("Handling ModelElement " + me.getName() + "[" + me.getMetaType() + "]", true);
                importModelElement(me);
            }
        } finally {
            SessionManager.getInstance().closeSession(project);
        }
    }

    private void importModelElement(ModelElement me) {
        if (this.isClass(me)) {
            importClass(me);
        } else if (this.isPackage(me)) {
            importPackage(me);
        }
    }

    private void importPackage(ModelElement pkg) {
        Package mdPkg = findOrCreatePackageByName(pkg.getName());
        stereotypesUpdater.update(mdPkg, pkg);
        for (ModelElement child : pkg.getChilds()) {
            importModelElement(child);
        }
    }

    private boolean isPackage(ModelElement me) {
        return me.getMetaType().equals("mPackage");
    }

    private boolean isClass(ModelElement me) {
        return me.getMetaType().equals("mClass");
    }


    private void importClass(ModelElement mClass) {
        ModelElement pkg = mClass.getParent();
        Package mdPkg = findOrCreatePackageByName(pkg.getName());
        Class clazz = findOrCreateClazz(pkg, mClass);
        for (ModelElement attr : mClass.getChilds()) {
            Property prop = findOrCreateProperty(clazz, attr.getName());
            oomPropertyUpdater.update(prop, attr);
        }
        stereotypesUpdater.update(clazz, mClass);
    }

    Class findOrCreateClazz(ModelElement pkg, ModelElement mClass) {
        return findOrCreateClazz(pkg.getName(), mClass.getName());
    }

    Class findOrCreateClazz(String pkgName, String className) {
        Package p = findOrCreatePackageByName(pkgName);
        Class c = Finder.byName().find(p, Class.class, className);
        if (c == null) {
            c = this.project.getElementsFactory().createClassInstance();
            c.setPackage(p);
            c.setName(className);
            c.setOwner(p);
        }
        return c;
    }

    private Property findOrCreateProperty(Class clazz, String name) {
        for (Property p : clazz.getOwnedAttribute()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        Property p = this.project.getElementsFactory().createPropertyInstance();
        p.setName(name);
        p.setOwner(clazz);
        return p;
    }

    Package findOrCreatePackageByName(String fqName) {
        Package parent = this.project.getPrimaryModel();
        for (String pkgName : fqName.split("\\.")) {
            Package pkg = Finder.byName().find(parent, Package.class, pkgName);
            if (pkg == null) {
                pkg = this.project.getElementsFactory().createPackageInstance();
                pkg.setOwner(parent);
                pkg.setName(pkgName);
            }
            parent = pkg;
        }

        return parent;
    }
}
