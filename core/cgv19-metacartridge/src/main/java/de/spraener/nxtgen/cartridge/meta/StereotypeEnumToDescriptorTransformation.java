package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.List;

public class StereotypeEnumToDescriptorTransformation extends StereotypeEnumToDescriptorTransformationBase {

    public static final String STEREOTYPE_LIST = "stereotypeList";

    @Override
    public void doTransformationIntern(MClass mc) {
        OOModel oom = (OOModel) mc.getModel();
        mc.putObject(STEREOTYPE_LIST,oom.getClassesByStereotype(MetaStereotypes.STEREOTYPE.getName()));
        mc.getStereotypes().add(new StereotypeImpl(MetaStereotypes.STEREOTYPEDESCRIPTOR.getName()));
    }

    public static List<MClass> getStereotypeList(MClass mc) {
        return (List<MClass>) mc.getObject(STEREOTYPE_LIST);
    }
}
