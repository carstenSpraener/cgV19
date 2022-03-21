package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;

public class EnsureTransformationDefinitionsTransformation extends EnsureTransformationDefinitionsTransformationBase {

    @Override
    public void doTransformationIntern(MClass mc) {
        String priority = mc.getTaggedValue(MetaCartridge.STEREOTYPE_TRANSFORMATION, MetaCartridge.TV_PRIORITY);
        if (priority == null || "".equals(priority)) {
            priority = "" + Integer.MAX_VALUE;
            Stereotype sType = StereotypeHelper.getStereotype(mc, MetaCartridge.STEREOTYPE_TRANSFORMATION);
            sType.setTaggedValue(MetaCartridge.TV_PRIORITY, priority);
        }
    }
}
