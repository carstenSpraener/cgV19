package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;

public class EnsureGeneratorDefinitionsTransformation extends EnsureGeneratorDefinitionsTransformationBase {

    @Override
    public void doTransformationIntern(MClass mc) {
        ensureCodeGeneratorDefinition(mc);
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
    }
}
