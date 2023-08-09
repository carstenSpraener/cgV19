package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MClassRef;

public class GeneratorGapTransformation implements Transformation {
    public static final String ORIGINAL_CLASS="originalClass";

    @Override
    public void doTransformation(ModelElement element) {
        if (element instanceof MClass mc  && !mc.getStereotypes().isEmpty() ) {
            MClass baseClazz = mc.getPackage().createMClass(mc.getName()+"Base");
            for( Stereotype sType : mc.getStereotypes() ) {
                Stereotype sTClone = cloneStereotype(sType, "Base");
                baseClazz.addStereotypes(sTClone);
            }
            baseClazz.setModel(mc.getModel());
            baseClazz.setInheritsFrom(mc.getInheritsFrom());
            mc.setInheritsFrom(new MClassRef(baseClazz.getFQName()));
            setOriginalClass(baseClazz, mc);
        }
    }

    public static MClass getOriginalClass(MClass mClass) {
        return (MClass)mClass.getObject(ORIGINAL_CLASS);
    }

    public static void setOriginalClass(MClass mClass, MClass originalClass) {
        mClass.putObject(ORIGINAL_CLASS, originalClass);
    }

    public static Stereotype cloneStereotype(Stereotype sType, String postFix) {
        StereotypeImpl clone = new StereotypeImpl(sType.getName()+postFix);
        for(TaggedValue tv : sType.getTaggedValues() ) {
            clone.setTaggedValue(tv.getName(), tv.getValue());
        }
        return clone;
    }
}
