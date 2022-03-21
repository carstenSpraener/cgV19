package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;

public class EnsureTransformationDefinitionsTransformation implements Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if (!(element instanceof MClass)) {
            return;
        }
        MClass mc = (MClass) element;
        if (!mc.hasStereotype(MetaCartridge.STEREOTYPE_TRANSFORMATION)) {
            return;
        }
        String priority = mc.getTaggedValue(MetaCartridge.STEREOTYPE_TRANSFORMATION, MetaCartridge.TV_PRIORITY);
        if (priority == null || "".equals(priority)) {
            priority = "" + Integer.MAX_VALUE;
            Stereotype sType = StereotypeHelper.getStereotype(mc, MetaCartridge.STEREOTYPE_TRANSFORMATION);
            sType.setTaggedValue(MetaCartridge.TV_PRIORITY, priority);
        }
    }
}
