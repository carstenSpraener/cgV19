package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MClassRef;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class GeneratorGapTransformation implements Transformation {
    public static final String ORIGINAL_CLASS="originalClass";
    private Predicate<MClass> shouldTransform = this::shouldTransform;
    private Supplier<MPackage> targetPackageSupplier = null;

    public GeneratorGapTransformation() {
    }

    public GeneratorGapTransformation(Predicate<MClass> shouldTransform ) {
        this.shouldTransform = shouldTransform;
    }

    public GeneratorGapTransformation(Supplier<MPackage> targetPackageSupplier) {
        this.targetPackageSupplier = targetPackageSupplier;
    }

    public GeneratorGapTransformation(Supplier<MPackage> targetPackageSupplier, Predicate<MClass> shouldTransform ) {
        this.shouldTransform = shouldTransform;
        this.targetPackageSupplier = targetPackageSupplier;
    }

    private boolean shouldTransform(MClass mc) {
        return !mc.getStereotypes().isEmpty();
    }

    @Override
    public void doTransformation(ModelElement element) {
        if ( element instanceof MClass mc  && this.shouldTransform.test(mc) ) {
            MPackage targetPkg = targetPackageSupplier != null ? targetPackageSupplier.get() :  mc.getPackage();
            MClass baseClazz = targetPkg.createMClass(mc.getName()+"Base");
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
