package de.spraener.nxtgen.groovy

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

    def toXML(prefix) {
        String propsDef = ""
        if (properties.size() > 0) {
            properties.entrySet().forEach({
                propsDef += " ${it.key}='${it.value}'"
            })
        }
        if (this.childs.size() == 0 && taggedValues.size() == 0) {
            println("${prefix}<${metaType} name='${name}' stereotype='${stereotype}'${propsDef}/>")
        } else {
            println("${prefix}<${metaType} name='${name}' stereotype='${stereotype}'${propsDef}>")
            taggedValues.entrySet().forEach({
                println("${prefix}  <taggedValue name='${it.key}' value='${it.value}'/>")
            })
            def myChilds = childs.clone()
            if (myChilds.size() > 0) {
                myChilds.forEach({ it.toXML($ { prefix } + '  ') })
            }
            println("${prefix}</${metaType}>")
        }
    }

    def name(value) {
        this.setName(value);
    }

    def stereotype(String name) {
        this.addStereotypes(GroovyModel.instance.createStereotype(name))
    }

    def stereotype(String name, Closure closure) {
        Stereotype stereotype = GroovyModel.instance.createStereotype(name);
        closure.delegate = stereotype;
        closure.resolveStrategy = Closure.DELEGATE_ONLY;
        stereotype.metaClass.taggedValue { String key, String val ->
            delegate.setTaggedValue(key, val);
        }
        closure();
        this.addStereotypes(stereotype);
    }

    def relation(Closure closure) {
        Relation rel = GroovyModel.instance.createRelation();
        closure.delegate = rel;
        closure.resolveStrategy = Closure.DELEGATE_ONLY;
        rel.metaClass.type { String typeName ->
            delegate.setType(typeName);
        }
        rel.metaClass.targetXmID { String id ->
            delegate.setTargetXmID(id);
        }
        rel.metaClass.targetType { String targetType ->
            delegate.setTargetType(targetType);
        }
        closure();
        this.relations.add(rel);
    }

    def methodMissing(String methodName, args) {
        def value = args[0];
        if (value instanceof Closure) {
            def ge = new GroovyElement();
            ge.metaType = methodName;
            this.childs.add(ge);

            value.delegate = ge;
            value.resolveStrategy = Closure.DELEGATE_FIRST;
            value();
        } else if (value instanceof String) {
            propertyMissing(methodName, value);
        }
    }

    def propertyMissing(name, value) {
        super.setProperty(name.toString(), value);
    }

    def propertyMissing(name) {
        super.getProperty(name);
    }

    def isAbstract(value) {
        super.setProperty("isAbstract", value.toString());
    }

    def metaType(value) {
        super.setProperty("metaType", value);
    }
}

class ModelDSL {
    def static Model make(closure) {
        GroovyElement root = new GroovyElement();

        closure.delegate = root;
        closure.resolveStrategy = Closure.DELEGATE_ONLY;
        closure();

        def model = new ModelImpl();
        try {
            root.getChilds().forEach({
                model.addModelElement(it);
            });
        }catch( Throwable e ) {
            e.printStackTrace();
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
