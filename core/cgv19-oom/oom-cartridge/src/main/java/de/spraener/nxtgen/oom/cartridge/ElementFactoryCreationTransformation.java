package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.ArrayList;
import java.util.List;

public class ElementFactoryCreationTransformation extends ElementFactoryCreationTransformationBase {
    private static final String MCLASS_LIST_ATTR_NAME = "mClassList";
    public static final String FACTORY_CLASS_NAME = "ModelElementFactoryImpl";

    private boolean executed = false;

    @Override
    public void doTransformationIntern(ModelElement aMe) {
        if (executed) {
            return;
        }
        try {
            Model m = aMe.getModel();
            MPackage modelPkg = null;
            MClass modelElementFactory = null;
            for (ModelElement me : m.getModelElements() ) {
                if( me instanceof MClass mc && mc.hasStereotype(OomStereoTypes.MELEMENT.getName()) ) {
                    if( modelPkg==null) {
                        modelPkg = ((MClass) me).getPackage();
                        modelElementFactory = modelPkg.createMClass(FACTORY_CLASS_NAME);
                        Stereotype sType = new StereotypeImpl(OomStereoTypes.MELEMENTFACTORY.getName());
                        modelElementFactory.addStereotypes(sType);
                    }
                    addMClassToElementFactory(modelElementFactory, mc);
                }
            }
        } finally {
            executed = true;
        }
    }

    private MClass addMClassToElementFactory(MClass modelElementFactory, MClass mc) {
        getMClassList(modelElementFactory).add(mc);
        return modelElementFactory;
    }

    public static List<MClass> getMClassList(MClass modelElementFactory) {
        List <MClass> mClassList = (List<MClass>) modelElementFactory.getObject(MCLASS_LIST_ATTR_NAME);
        if( mClassList==null ) {
            mClassList = new ArrayList<>();
            modelElementFactory.putObject(MCLASS_LIST_ATTR_NAME, mClassList);
        }
        return mClassList;
    }

    public static String getModelClassToModelElementName(MClass mc) {
        if( !StereotypeHelper.hasStereotype(mc, OomStereoTypes.MELEMENT.getName())) {
            return null;
        }
        String modelElementName = StereotypeHelper.getStereotype(mc,OomStereoTypes.MELEMENT.getName()).getTaggedValue("modelElementName");
        if( modelElementName==null ) {
            modelElementName = mc.getName().substring(0,1).toLowerCase() + mc.getName().substring(1);
        }
        return modelElementName;
    }
}
