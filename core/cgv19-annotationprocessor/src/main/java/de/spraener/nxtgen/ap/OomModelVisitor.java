package de.spraener.nxtgen.ap;

import de.spraener.nxtgen.model.Relation;
import de.spraener.nxtgen.model.impl.RelationImpl;
import de.spraener.nxtgen.oom.model.MAbstractModelElement;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MOperation;
import de.spraener.nxtgen.oom.model.MParameter;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementKindVisitor14;
import java.util.List;
import java.util.Set;

public class OomModelVisitor extends ElementKindVisitor14<Void, MAbstractModelElement> {
    private final OOModel oom;
    private final String rootPackage;

    public OomModelVisitor(OOModel oom, String rootPackage) {
        this.oom = oom;
        this.rootPackage = rootPackage == null ? "" : rootPackage;
    }

    @Override
    public Void visitType(TypeElement e, MAbstractModelElement parent) {
        // Verschachtelte Typen werden nicht modelliert (das OOM kennt kein Nesting)
        if (parent != null && !(parent instanceof MPackage)) {
            return null;
        }
        // Logik für Klassen / Interfaces
        MPackage pkg = (MPackage)parent;
        if( pkg == null ) {
            String pkgName = toPkgName(e);
            pkg = findPkgByName(pkgName);
            if( pkg==null) {
                pkg = new MPackage();
                pkg.setName(pkgName);
                oom.getChilds().add(pkg);
                pkg.setModel(oom);
            }
        }
        MClass mClass = pkg.createMClass(e.getSimpleName().toString());
        StereotypeMapper.apply(e, mClass);
        addInheritanceRelations(e, mClass);

        // Weiterabstieg zu Feldern, Methoden etc.
        for (Element enclosed : e.getEnclosedElements()) {
            enclosed.accept(this, mClass);
        }
        return null;
    }

    private MPackage findPkgByName(String pkgName) {
        return oom.getModelElements().stream()
                .filter(me -> me != null)
                .filter(me->"package".equals(me.getMetaType()) ||me instanceof MPackage)
                .map(e -> (MPackage)e)
                .filter(pkg -> pkg.getFQName().equals(pkgName))
                .findFirst().orElse(null);
    }

    private String toPkgName(TypeElement e) {
        String fqName = e.getQualifiedName().toString();
        return fqName.substring(0, fqName.lastIndexOf('.'));
    }

    private void addInheritanceRelations(TypeElement e, MClass mClass) {
        if (e.getKind() == ElementKind.INTERFACE) {
            // Interfaces: alle Super-Interfaces sind Verallgemeinerungen
            for (TypeMirror itf : e.getInterfaces()) {
                addRelation(mClass, "extends", toFQName(itf));
            }
            return;
        }

        // Klassen: direkter Superclass (implizites java.lang.Object wird nicht modelliert)
        if (e.getSuperclass() instanceof DeclaredType superc) {
            String fqName = toFQName(superc);
            if (!"java.lang.Object".equals(fqName)) {
                addRelation(mClass, "extends", fqName);
            }
        }
        for (TypeMirror itf : e.getInterfaces()) {
            if (itf instanceof DeclaredType dt) {
                addRelation(mClass, "implements", toFQName(dt));
            }
        }
    }

    private void addRelation(MClass mClass, String type, String targetType) {
        Relation rel = new RelationImpl();
        rel.setType(type);
        rel.setTargetType(targetType);
        mClass.addRelations(rel);
    }

    private String toFQName(TypeMirror type) {
        if (type instanceof DeclaredType dt && dt.asElement() instanceof TypeElement te) {
            return te.getQualifiedName().toString();
        }
        return type.toString();
    }

    @Override
    public Void visitVariable(VariableElement e, MAbstractModelElement parent) {
        // Logik für Attribute; Parameter und lokale Variablen werden ignoriert
        if (e.getKind() != ElementKind.FIELD || !(parent instanceof MClass mClass)) {
            return null;
        }

        TypeMirror type = e.asType();
        boolean isToN = false;
        if (type instanceof DeclaredType dt && isCollection(dt)) {
            // 1..n-Beziehung: Collection (List/Set) mit Elementtyp
            isToN = true;
            List<? extends TypeMirror> typeArgs = dt.getTypeArguments();
            if (!typeArgs.isEmpty()) {
                type = typeArgs.get(0);
            }
        }

        if (isModelType(type)) {
            // Typ liegt unterhalb des Root-Packages → Assoziation statt Attribut
            MAssociation assoc = new MAssociation();
            assoc.setName(e.getSimpleName().toString());
            TypeElement targetClass = (TypeElement) ((DeclaredType) type).asElement();
            assoc.setType(targetClass.getQualifiedName().toString());
            assoc.setMultiplicity(isToN ? "*" : "1");
            assoc.setParent(mClass);
            assoc.setModel(mClass.getModel());
            mClass.getChilds().add(assoc);
            StereotypeMapper.apply(e, assoc);
        } else {
            MAttribute attr = mClass.createAttribute(e.getSimpleName().toString(), e.asType().toString());
            attr.setProperty("visibility", toVisibility(e));
            StereotypeMapper.apply(e, attr);
        }
        return null;
    }

    private boolean isModelType(TypeMirror type) {
        if (!(type instanceof DeclaredType dt)) {
            return false; // Primitiven etc. → normales Attribut
        }
        if (!(dt.asElement().getEnclosingElement() instanceof PackageElement pkg) || pkg.isUnnamed()) {
            return false;
        }
        String pkgName = pkg.getQualifiedName().toString();
        if (rootPackage.isEmpty()) {
            return false;
        }
        return pkgName.equals(rootPackage) || pkgName.startsWith(rootPackage + ".");
    }

    private boolean isCollection(DeclaredType dt) {
        String name = dt.asElement().getSimpleName().toString();
        return "List".equals(name) || "Set".equals(name) || "Collection".equals(name);
    }

    private String toVisibility(VariableElement e) {
        Set<Modifier> modifiers = e.getModifiers();
        if (modifiers.contains(Modifier.PUBLIC)) {
            return "public";
        }
        if (modifiers.contains(Modifier.PROTECTED)) {
            return "protected";
        }
        // Ohne expliziten Modifier ist das Feld package-private; das lässt sich in
        // generiertem Java-Code nicht darstellen, daher wie bisher "private"
        return "private";
    }

    @Override
    public Void visitExecutable(ExecutableElement e, MAbstractModelElement parent) {
        // Logik für Methoden; Konstruktoren werden ignoriert
        if (e.getKind() != ElementKind.METHOD || !(parent instanceof MClass mClass)) {
            return null;
        }

        MOperation op = mClass.createOperation(e.getSimpleName().toString());
        op.setType(e.getReturnType().toString());
        StereotypeMapper.apply(e, op);

        for (VariableElement param : e.getParameters()) {
            MParameter p = op.createParameter(param.getSimpleName().toString(), param.asType().toString());
            StereotypeMapper.apply(param, p);
        }
        return null;
    }
}
