package de.spraener.nxtgen.groovy

import de.spraener.nxtgen.ModelElementFactory
import de.spraener.nxtgen.NextGen
import de.spraener.nxtgen.model.Model
import de.spraener.nxtgen.model.ModelElement
import de.spraener.nxtgen.model.Relation
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.model.impl.ModelElementImpl
import de.spraener.nxtgen.model.impl.ModelImpl
import groovy.transform.Canonical


@Canonical
class GroovyModel extends ModelImpl {
    static instance = new GroovyModel();

    @Override
    ModelElement createModelElement() {
        return new GroovyElement();
    }

    static GroovyModel getInstance() {
        return instance;
    }
}

@Canonical
class GroovyElement extends ModelElementImpl {
    ModelElementFactory elementFactory = NextGen.getActiveLoader().getModelElementFactory();
    ModelElementImpl myModelElement;

    GroovyElement() {
        this.myModelElement = this;
    }

    GroovyElement(String elementName) {
        myModelElement = elementFactory.createModelElement(elementName);
        if( myModelElement==null) {
            myModelElement = this;
        }
    }

    def name(value) {
        myModelElement.setName(value);
    }

    def stereotype(String name) {
        myModelElement.addStereotypes(GroovyModel.instance.createStereotype(name))
    }

    def stereotype(String name, Closure closure) {
        Stereotype stereotype = GroovyModel.instance.createStereotype(name);
        closure.delegate = stereotype;
        closure.resolveStrategy = Closure.DELEGATE_ONLY;
        stereotype.metaClass.taggedValue { String key, String val ->
            delegate.setTaggedValue(key, val);
        }
        closure();
        myModelElement.addStereotypes(stereotype);
    }

    def relation(Closure closure) {
        Relation rel = GroovyModel.instance.createRelation();
        closure.delegate = rel;
        closure.resolveStrategy = Closure.DELEGATE_ONLY;
        rel.metaClass.type { String typeName ->
            delegate.setType(typeName);
        }
        rel.metaClass.targetXmID { String id ->
            delegate.setTargetXmiID(id);
        }
        rel.metaClass.targetType { String targetType ->
            delegate.setTargetType(targetType);
        }
        closure();
        myModelElement.relations.add(rel);
    }

    def methodMissing(String methodName, args) {
        def value = args[0];
        if (value instanceof Closure) {
            ModelElement ge = new GroovyElement(methodName)
            ge.metaType = methodName;
            myModelElement.childs.add(ge.myModelElement);
            ge.myModelElement.setParent(myModelElement);

            value.delegate = ge;
            value.resolveStrategy = Closure.DELEGATE_FIRST;
            value();
            ge.myModelElement.postDefinition();
        } else if (value instanceof String) {
            propertyMissing(methodName, value);
        }
    }

    def propertyMissing(name, value) {
        myModelElement.setProperty(name.toString(), value);
    }

    def propertyMissing(name) {
        myModelElement.getProperty(name);
    }

    def isAbstract(value) {
        myModelElement.setProperty("isAbstract", value.toString());
    }

    def metaType(value) {
        myModelElement.setProperty("metaType", value);
    }
}

/**
 * A base implementation of an Model-DSL
 */
class ModelDSL {
    def static Model make(closure) {
        GroovyElement root = new GroovyElement();

        closure.delegate = root;
        closure.resolveStrategy = Closure.DELEGATE_ONLY;
        closure();

        def model = NextGen.getActiveLoader().modelElementFactory.createModel();
        if( model==null) {
            model = new ModelImpl();
        }
        try {
            root.getChilds().forEach({
                model.addModelElement(it);
            });
        }catch( Throwable e ) {
            // Backward compatibility to groovy 2.0.1 as used in magic draw
            for( Object it : root.getChilds()){
                model.addModelElement(it);
            }
        }
        return (Model) model;
    }
}

println(ModelDSL.make {
    mPackage {
        name 'de.spraener.test'
    mClass {
        mAttribute {
            name 'Name'
        }
        mOperation {
            name 'helloWorld'
            type 'String'
            mParameter {
                name 'name'
                type 'String'
            }
            mParameter {
                name 'firstName'
                type 'String'
            }
        }
        mActivity {
            name 'validate'
            mControlFlow {
                targetId '0815'
            }
        }
    }
    }
})
