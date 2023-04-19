package de.csp.cgv19.mdplugin.oom;

import com.google.gson.Gson;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
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

    private boolean isOperation(ModelElement me) {
        return me.getMetaType().equals("mOperation");
    }

    private boolean isParameter(ModelElement me) {
        return me.getMetaType().equals("mParameter");
    }

    private boolean isAttribute(ModelElement me) {
        return me.getMetaType().equals("mAttribute");
    }

    private void importClass(ModelElement mClass) {
        ModelElement pkg = mClass.getParent();
        Package mdPkg = findOrCreatePackageByName(pkg.getName());
        Class clazz = findOrCreateClazz(pkg, mClass);
        for (ModelElement child : mClass.getChilds()) {
            if (isAttribute(child)) {
                Property prop = findOrCreateProperty(clazz, child.getName());
                oomPropertyUpdater.update(prop, child);
            }
            if( isOperation(child) ) {
                Operation op = findOrCreateOperationByName(clazz, child.getName());
                Type returnType = oomPropertyUpdater.findOrCreateType(child.getProperty("type"));
                updateReturnType(op,returnType);
                for( ModelElement opChild : child.getChilds() ) {
                    if( isParameter( opChild) ) {
                        Parameter p = findOrCreateParameter(op, opChild.getName());
                        p.setType(oomPropertyUpdater.findOrCreateType(opChild.getProperty("type")));
                    }
                }
            }
        }
        stereotypesUpdater.update(clazz, mClass);
    }

    void updateReturnType(Operation op, Type returnType) {
        for( Parameter p : op.getOwnedParameter() ) {
            if( ParameterDirectionKindEnum.RETURN==p.getDirection()) {
                p.setType(returnType);
                return;
            }
        }
        Parameter p = this.project.getElementsFactory().createParameterInstance();
        p.setDirection(ParameterDirectionKindEnum.RETURN);
        p.setOwner(op);
    }

    Parameter findOrCreateParameter(Operation op, String name) {
        for( Parameter p : op.getOwnedParameter() ) {
            if( p.getName().equals(name)) {
                return p;
            }
        }
        Parameter p = this.project.getElementsFactory().createParameterInstance();
        p.setName(name);
        p.setOwner(op);
        return p;
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

    Operation findOrCreateOperationByName(Class clazz, String name) {
        for( Operation op : clazz.getOwnedOperation() ) {
            if(op.getName().equals(name)) {
                return op;
            }
        }
        Operation op = this.project.getElementsFactory().createOperationInstance();
        op.setName(name);
        op.setOwner(clazz);
        return op;
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
