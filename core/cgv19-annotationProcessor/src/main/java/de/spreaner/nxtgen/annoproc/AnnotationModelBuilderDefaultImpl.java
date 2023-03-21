package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.ModelImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;

import javax.lang.model.element.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationModelBuilderDefaultImpl implements AnnotationModelBuilder {
    private final ModelImpl model = new ModelImpl();

    private final Map<Element, ModelElementImpl> element2ModelElementMap = new HashMap<>();
    public boolean contains(Element e) {
        return element2ModelElementMap.containsKey(e);
    }

    @Override
    public ModelImpl getModel() {
        return model;
    }

    @Override
    public void handleElement(Element e) {
        if( contains(e) ) {
            return;
        }
        ModelElementImpl modelElement = createModelElement(e);
        element2ModelElementMap.put(e,modelElement);
    }

    private ModelElementImpl createModelElement(Element e) {
        Element parent = e.getEnclosingElement();
        if( !isKnownType(parent) ) {
            parent = null;
        }
        if( parent != null ) {
            handleElement(parent);
        }
        ModelElementImpl me = new ModelElementImpl();
        if(e instanceof PackageElement pkgE) {
            me.setMetaType("mPackage");
            me.setName(pkgE.getQualifiedName().toString());
            if( parent != null ) {
                ModelElementImpl parentPkg = element2ModelElementMap.get(parent);
                parentPkg.addChilds(me);
            } else {
                model.addModelElement(me);
            }
        }
        if(e instanceof TypeElement te) {
            me.setMetaType("class");
            me.setName(te.getSimpleName().toString());
            element2ModelElementMap.put(e,me);
            if( parent != null ) {
                ModelElementImpl parentPkg = element2ModelElementMap.get(parent);
                parentPkg.addChilds(me);
            } else {
                model.addModelElement(me);
            }
            createFields(me,te.getEnclosedElements());
        }
        me.setModel(this.model);
        if( parent != null ) {
            me.setParent(element2ModelElementMap.get(parent));
        }
        applyAnnotations(e,me);
        return me;
    }

    private void createFields(ModelElementImpl me, List<? extends Element> enclosedElements) {
        for( Element e : enclosedElements ) {
            if(e instanceof VariableElement ve) {
                ModelElementImpl attr = new ModelElementImpl();
                me.addChilds(attr);

                attr.setMetaType("mAttribute");
                attr.setName(ve.getSimpleName().toString());
                attr.setProperty("type", ve.asType().toString());
            }
        }
    }

    private boolean isKnownType(Element parent) {
        if( parent instanceof TypeElement ) {
            return true;
        }
        return parent instanceof PackageElement;
    }

    private void applyAnnotations(Element e, ModelElementImpl me) {
        for(AnnotationMirror a : e.getAnnotationMirrors() ) {
            String stereotypeName = a.getAnnotationType().asElement().getSimpleName().toString();
            StereotypeImpl st = new StereotypeImpl(stereotypeName);
            me.getStereotypes().add(st);
        }
    }


}
