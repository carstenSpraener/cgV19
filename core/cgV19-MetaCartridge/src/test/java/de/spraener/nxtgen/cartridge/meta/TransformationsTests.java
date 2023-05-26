package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransformationsTests extends AbstractOOModelTest {

    @Test
    public void testCartridgeBaseClassAdded() {
        // Given: A class with stereotype "cgv19Cartridge"
        oomObjectMother.createDefaultOOModel();
        MClass mc = oomObjectMother.createClass("DefaultClass",
                c -> c.addStereotypes(new StereotypeImpl(MetaStereotypes.CGV19CARTRIDGE.getName()))
                );

        // when: Calling CartridgeBaseForCartridgeTransformation
        CartridgeBaseForCartridgeTransformation uut = new CartridgeBaseForCartridgeTransformation();
        uut.doTransformation(mc);

        // than:
        // a new class appears with the same name but with postfix "Base"
        // in the same package...
        MClass baseClass = oomObjectMother.getModel().findClassByName(mc.getFQName() + "Base");
        assertNotNull(baseClass);
        // ...with stereotype "cgv19CartridgeBase"...
        assertTrue(StereotypeHelper.hasStereotye(baseClass, MetaCartridge.STYPE_CGV19CARTRIDGE_BASE));
        // ...and the original class inherits from this.
        assertTrue(mc.getInheritsFrom().getFullQualifiedClassName().equals(baseClass.getFQName()));
    }

    @Test
    public void testServiceLocatorClassAdded() {
        CartridgeServicesLocatorTransformation uut = new CartridgeServicesLocatorTransformation();

        // Given: the standard model
        MClass mc = oomObjectMother.createClass("DefaultClass",
                c -> c.addStereotypes(new StereotypeImpl(MetaStereotypes.CGV19CARTRIDGE.getName()))
        );

        uut.doTransformation(mc);

        MClass clazz = oomObjectMother.getModel().findClassByName("pkg.CartridgeServiceLocator");
        assertNotNull(clazz);
        assertTrue(StereotypeHelper.hasStereotye(clazz, MetaCartridge.STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION));
    }

    @Test
    public void testPriorityDefaultSet() throws Exception {
        EnsureTransformationDefinitionsTransformation uut = new EnsureTransformationDefinitionsTransformation();
        MClass mc = oomObjectMother.createClass("ATransformation",
                clazz -> clazz.addStereotypes(new StereotypeImpl(MetaStereotypes.TRANSFORMATION.getName()))
        );

        uut.doTransformation(mc);
        Stereotype stype = StereotypeHelper.getStereotype(mc, MetaStereotypes.TRANSFORMATION.getName());
        String priority = stype.getTaggedValue(MetaCartridge.TV_PRIORITY);
        assertEquals("" + Integer.MAX_VALUE, priority);
    }

    @Test
    public void testPrioritySetNotChanged() throws Exception {
        EnsureTransformationDefinitionsTransformation uut = new EnsureTransformationDefinitionsTransformation();
        MClass mc = oomObjectMother.createClass("ATransformation",
                clazz -> clazz.addStereotypes(new StereotypeImpl(MetaStereotypes.TRANSFORMATION.getName())),
                clazz -> StereotypeHelper.getStereotype(clazz, MetaStereotypes.TRANSFORMATION.getName()).setTaggedValue("priority", "7")
        );

        uut.doTransformation(mc);
        Stereotype stype = StereotypeHelper.getStereotype(mc, MetaStereotypes.TRANSFORMATION.getName());
        String priority = stype.getTaggedValue(MetaCartridge.TV_PRIORITY);
        assertEquals("7", priority);
    }

    @Test
    public void testGeneratorDefinitionsTransformation() throws Exception {
        EnsureGeneratorDefinitionsTransformation uut = new EnsureGeneratorDefinitionsTransformation();
        // Given:
        // a class with Stereotype "CodeGenerator"
        String sTypeName = MetaStereotypes.CODEGENERATOR.getName();
        MClass generator = oomObjectMother.createClass("AGenerator",
                c -> c.addStereotypes(new StereotypeImpl(sTypeName,
                        s -> s.setTaggedValue(MetaCartridge.TV_REQUIRED_STEREOTYPE, "ARequiredStereotype")
                        )
                )
        );

        // when calling the transformation
        uut.doTransformation(generator);

        // then
        // The class has a new TaggedValue 'groovyScript' on Stereotype 'CodeGenerator'
        Stereotype sType = StereotypeHelper.getStereotype(generator, sTypeName);
        String scriptName = sType.getTaggedValue(MetaCartridge.TV_TEMPLATE_SCRIPT);
        assertEquals("/pkg/ATemplate.groovy", scriptName);

        // a new class appears to present the groovy script
        MClass groovyScriptClazz = model.findClassByName(generator.getFQName() + MetaCartridge.STYPE_GROOVY_SCRIPT);
        assertNotNull(groovyScriptClazz);
        // The new class has a stereotyope of 'GroovyScript'
        assertTrue(groovyScriptClazz.hasStereotype(MetaCartridge.STYPE_GROOVY_SCRIPT));
        // and a taggedValue of 'scriptFile' with value "/pkg/ATemplate.groovy"
        Stereotype groovyScriptSType = StereotypeHelper.getStereotype(groovyScriptClazz, MetaCartridge.STYPE_GROOVY_SCRIPT);
        assertEquals("/pkg/ATemplate.groovy", groovyScriptSType.getTaggedValue(MetaCartridge.TV_SCRIPT_FILE));
        // and the fq-Name of generator class on tagged value 'generatorClass'
        assertEquals(generator.getFQName(), groovyScriptSType.getTaggedValue(MetaCartridge.TV_GENERATOR_CLASS));
    }


    @Test
    public void testGeneratorDefinitionsTransformationRequiresSTypeViaUsage() throws Exception {
        EnsureGeneratorDefinitionsTransformation uut = new EnsureGeneratorDefinitionsTransformation();
        // Given:
        MClass stypeClass = oomObjectMother.createClass("AStereoType",
                c -> c.addStereotypes(new StereotypeImpl(MetaStereotypes.STEREOTYPE.getName()))
        );
        // a class with Stereotype "CodeGenerator"
        String sTypeName = MetaStereotypes.CODEGENERATOR.getName();
        MClass generator = oomObjectMother.createClass("AGenerator",
                c -> c.addStereotypes(new StereotypeImpl(sTypeName)),
                c -> c.getUsages().add(oomObjectMother.createUsage(c, stypeClass))
        );

        uut.doTransformation(generator);

        Stereotype sType = StereotypeHelper.getStereotype(generator, MetaStereotypes.CODEGENERATOR.getName());
        assertEquals(stypeClass.getName(), sType.getTaggedValue(MetaCartridge.TV_REQUIRED_STEREOTYPE));
    }

    @Test
    public void testRemoveModelRootTransformation() throws Exception {
        MPackage metaDSLPkg = oomObjectMother.createPackage("META-DSL",
            p -> p.addStereotypes(new StereotypeImpl(MetaStereotypes.MODELROOT.getName()))
        );
        MPackage dePkg = oomObjectMother.createSubPackage(metaDSLPkg, "de" );
        MClass testClass = oomObjectMother.createClass(dePkg, "TestClass");
        assertEquals("META-DSL.de.TestClass", testClass.getFQName());

        RemoveModelRootPackage rr = new RemoveModelRootPackage();
        rr.doTransformation(metaDSLPkg);
        assertEquals("de.TestClass", testClass.getFQName());
    }
}
