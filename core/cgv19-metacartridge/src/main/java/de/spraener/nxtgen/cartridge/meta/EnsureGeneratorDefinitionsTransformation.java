package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.MUsage;
import de.spraener.nxtgen.oom.model.OOModel;

public class EnsureGeneratorDefinitionsTransformation extends EnsureGeneratorDefinitionsTransformationBase {
    public static final String OO_MODEL_MOTHER = "OOModelMother";
    private static MPackage rootPkg = null;

    @Override
    public void doTransformationIntern(MClass mc) {
        ensureCodeGeneratorDefinition(mc);
        if( "MClass".equals(mc.getTaggedValue(MetaStereotypes.CODEGENERATOR.getName(), "generatesOn")) ) {
            MClass modelMother = createOrFindModelMother(mc);
            OOModelBuilder.createMClass(mc.getPackage(), mc.getName() + "Test",
                    c -> OOModelBuilder.createStereotype(c, MetaStereotypes.CODEGENERATORTEST.getName()),
                    c -> GeneratorGapTransformation.setOriginalClass(c, mc),
                    c -> c.putObject(OO_MODEL_MOTHER, modelMother)
            );
        }
    }

    private MClass createOrFindModelMother(MClass mc) {
        OOModel model = (OOModel)mc.getModel();
        MPackage pkg = getRootPkg(model);
        MClass modelMother = model.findClassByName(rootPkg.getFQName()+"."+OO_MODEL_MOTHER);
        if( modelMother == null ) {
            OOModelBuilder.createMClass(pkg, OO_MODEL_MOTHER,
                    c -> OOModelBuilder.createStereotype(c, "ObjectModelMother")
            );
        }
        return modelMother;
    }

    private static MPackage getRootPkg(OOModel model) {
        if( rootPkg == null ) {
            MClass cartridge = model.getClassesByStereotype(MetaStereotypes.CGV19CARTRIDGE.getName()).get(0);
            if (cartridge != null) {
                rootPkg = cartridge.getPackage();
            } else {
                rootPkg = (MPackage) model.getChilds().stream().filter(me -> me instanceof MPackage).findFirst().orElse(null);
            }
        }
        return rootPkg;
    }

    private void ensureCodeGeneratorDefinition(MClass mc) {
        String templateScript = mc.getTaggedValue(MetaCartridge.STEREOTYPE_CODE_GENERATOR, MetaCartridge.TV_TEMPLATE_SCRIPT);
        if (templateScript == null || "".equals(templateScript)) {
            templateScript = "/" + mc.getPackage().getName() + "/" + mc.getName();
            if (templateScript.endsWith("Generator")) {
                templateScript = templateScript.substring(0, templateScript.length() - "Generator".length());
                templateScript += "Template";
            }
            templateScript += ".groovy";
            StereotypeHelper.getStereotype(mc, MetaCartridge.STEREOTYPE_CODE_GENERATOR).setTaggedValue(MetaCartridge.TV_TEMPLATE_SCRIPT, templateScript);
        }
        if (templateScript.endsWith(".groovy")) {
            OOModel oom = (OOModel) mc.getModel();
            MClass scrptClass = mc.getPackage().createMClass(mc.getName() + MetaCartridge.STYPE_GROOVY_SCRIPT);
            Stereotype sType = new StereotypeImpl(MetaCartridge.STYPE_GROOVY_SCRIPT);
            scrptClass.addStereotypes(sType);
            sType.setTaggedValue(MetaCartridge.TV_SCRIPT_FILE, templateScript);
            sType.setTaggedValue(MetaCartridge.TV_GENERATOR_CLASS, mc.getFQName());
        }

        String requiredStereotype = StereotypeHelper.getStereotype(mc, MetaCartridge.STEREOTYPE_CODE_GENERATOR).getTaggedValue(MetaCartridge.TV_REQUIRED_STEREOTYPE);
        if( requiredStereotype==null || "".equals(requiredStereotype) ) {
            OOModel model = (OOModel)mc.getModel();
            MClass targetUsage = mc.getUsages().stream()
                    .filter(usage -> usage.getTarget()!=null)
                    .map(usage -> usage.getTarget())
                    .map(cName -> model.findClassByName(cName))
                    .filter( c -> c.hasStereotype(MetaCartridge.STEREOTYPE_NAME))
                    .findFirst()
                    .orElse(null);
            if( targetUsage != null ) {
                StereotypeHelper.getStereotype(mc, MetaCartridge.STEREOTYPE_CODE_GENERATOR)
                        .setTaggedValue(MetaCartridge.TV_REQUIRED_STEREOTYPE, targetUsage.getName());
            }
        }
    }

    public static MClass getOOModelMother(MClass mc) {
        return (MClass)mc.getObject(OO_MODEL_MOTHER);
    }
}
