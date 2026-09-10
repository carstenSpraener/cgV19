package de.spraener.nxtgen.ap;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.ap.meta.Application;
import de.spraener.nxtgen.ap.meta.Entity;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Relation;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MOperation;
import de.spraener.nxtgen.oom.model.MParameter;
import de.spraener.nxtgen.oom.model.MPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationProcessorModelLoaderTest {

    @Test
    void canHandleJavaApProtocol() {
        AnnotationProcessorModelLoader loader = new AnnotationProcessorModelLoader();
        assertTrue(loader.canHandle("java-ap://src/model/java"));
        assertFalse(loader.canHandle("some/file.oom"));
    }

    @Test
    void discoversStereotypeAnnotationsWithoutExplicitConfiguration() {
        // Ohne withAnnotations: der Processor muss die @Stereotype-markierten
        // Annotationen selbst per Wildcard + Runtime-Check entdecken
        AnnotationProcessorModelLoader loader = new AnnotationProcessorModelLoader();

        Model model = loader.loadModel("java-ap://src/test/model");

        assertNotNull(model, "Loaded model must not be null");
        ModelElement pkg = model.getModelElements().get(0);
        assertEquals("mPackage", pkg.getMetaType());
        assertEquals("de.spraener.nxtgen.ap.model", pkg.getName());

        MClass person = pkg.getChilds().stream()
                .filter(c -> c instanceof MClass)
                .map(c -> (MClass)c)
                .filter(c -> "Person".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(person, "Person class must exist");
        assertEquals("person", person.getTaggedValue("Entity", "tableName"));
    }

    @Test
    void loadsModelFromJavaSources() {
        AnnotationProcessorModelLoader loader = new AnnotationProcessorModelLoader()
                .withAnnotations(
                        Entity.class,
                        Application.class
                )
                ;
        NextGen.addModelLoader(loader);

        Model model = loader.loadModel("java-ap://src/test/model");

        assertNotNull(model, "Loaded model must not be null");
        List<ModelElement> elements = model.getModelElements();
        assertFalse(elements.isEmpty(), "Model must contain at least one element");

        assertTrue( model.getModelElements().stream().filter(me -> me instanceof MPackage).count()==1, "Das Root-Paket dar nur einmal vorhanden sein.");

        ModelElement pkg = elements.get(0);
        assertEquals("mPackage", pkg.getMetaType());
        assertEquals("de.spraener.nxtgen.ap.model", pkg.getName());

        List<ModelElement> classes = pkg.getChilds();
        assertTrue(classes.size()>=2, "Package must contain at least one class");

        assertFalse(classes.stream().anyMatch(c -> c instanceof MClass && "Nested".equals(c.getName())),
                "Nested classes must not be modeled");

        MClass person = classes.stream()
                .filter(c -> c instanceof MClass)
                .map(c -> (MClass)c)
                .filter(c -> "Person".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(person, "Person class must exist");

        List<MAttribute> attrs = person.getAttributes();
        assertEquals(2, attrs.size(), "Person must have two attributes");

        MAttribute nameAttr = attrs.stream().filter(a -> "name".equals(a.getName())).findFirst().orElse(null);
        assertNotNull(nameAttr, "Attribute 'name' must exist");
        assertEquals("java.lang.String", nameAttr.getType());
        assertEquals("private", nameAttr.getProperty("visibility"));

        MAttribute ageAttr = attrs.stream().filter(a -> "age".equals(a.getName())).findFirst().orElse(null);
        assertNotNull(ageAttr, "Attribute 'age' must exist");
        assertEquals("int", ageAttr.getType());

        List<MAssociation> assocs = person.getAssociations();
        assertEquals(2, assocs.size(), "Person must have two associations");

        MAssociation addressAssoc = assocs.stream().filter(a -> "address".equals(a.getName())).findFirst().orElse(null);
        assertNotNull(addressAssoc, "Association 'address' must exist");
        assertEquals("de.spraener.nxtgen.ap.model.Address", addressAssoc.getType());
        assertEquals("1", addressAssoc.getMultiplicity());

        MAssociation addressesAssoc = assocs.stream().filter(a -> "addresses".equals(a.getName())).findFirst().orElse(null);
        assertNotNull(addressesAssoc, "Association 'addresses' must exist");
        assertEquals("de.spraener.nxtgen.ap.model.Address", addressesAssoc.getType());
        assertEquals("*", addressesAssoc.getMultiplicity());

        List<MOperation> ops = person.getOperations();
        assertEquals(2, ops.size(), "Person must have two operations");

        MOperation getNameOp = ops.stream().filter(o -> "getName".equals(o.getName())).findFirst().orElse(null);
        assertNotNull(getNameOp, "Operation 'getName' must exist");
        assertEquals("java.lang.String", getNameOp.getType());
        assertTrue(getNameOp.getParameters().isEmpty(), "getName must have no parameters");

        MOperation setAgeOp = ops.stream().filter(o -> "setAge".equals(o.getName())).findFirst().orElse(null);
        assertNotNull(setAgeOp, "Operation 'setAge' must exist");
        assertEquals("void", setAgeOp.getType());
        List<MParameter> params = setAgeOp.getParameters();
        assertEquals(1, params.size(), "setAge must have one parameter");
        assertEquals("age", params.get(0).getName());
        assertEquals("int", params.get(0).getType());

        List<Relation> relations = person.getRelations();
        assertTrue(relations.stream().anyMatch(r -> "extends".equals(r.getType())
                        && "de.spraener.nxtgen.ap.model.BasePerson".equals(r.getTargetType())),
                "Person must extend BasePerson");
        assertTrue(relations.stream().anyMatch(r -> "implements".equals(r.getType())
                        && "de.spraener.nxtgen.ap.model.Named".equals(r.getTargetType())),
                "Person must implement Named");

        Stereotype entitySt = person.getStereotypes().stream()
                .filter(st -> "Entity".equals(st.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(entitySt, "Person must have stereotype 'Entity'");
        assertEquals("person", entitySt.getTaggedValue("tableName"));

        MClass demoApp = classes.stream()
                .filter(c -> c instanceof MClass)
                .map(c -> (MClass)c)
                .filter(c -> "DemoApp".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(demoApp, "DemoApp class must exist");

        Stereotype appSt = demoApp.getStereotypes().stream()
                .filter(st -> "Application".equals(st.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(appSt, "DemoApp must have stereotype 'Application'");
        assertTrue(appSt.getTaggedValues().isEmpty(), "Application has no attributes, hence no tagged values");

    }
}
