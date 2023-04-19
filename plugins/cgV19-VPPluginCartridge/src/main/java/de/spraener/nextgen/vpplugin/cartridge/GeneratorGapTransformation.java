package de.spraener.nextgen.vpplugin.cartridge;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class GeneratorGapTransformation extends GeneratorGapTransformationBase {
    public static final String BASE_FROM_REF = "GeneratorGapBaseFromProperty";

    @Override
    public void doTransformationIntern(MClass mClass) {
        if( !StereotypeHelper.hasStereotye(mClass, "Exporter") ) {
            return;
        }
        NextGen.LOGGER.info("Running GeneratorGapTransformation on "+mClass.getFQName());
        MClass mBase = mClass.getPackage().createMClass(mClass.getName()+"Base");
        mBase.setParent(mClass.getParent());
        mBase.setName(mClass.getName()+"Base");
        mBase.setInheritsFrom(mClass.getInheritsFrom());
        for(Stereotype sType : mClass.getStereotypes() ) {
            Stereotype stBase = new StereotypeImpl(sType.getName()+"Base");
            mBase.getStereotypes().add(stBase);
            stBase.setTaggedValue("oomType", StereotypeHelper.getStereotype(mClass, "Exporter").getTaggedValue("oomType"));
        }
        mBase.setProperty(BASE_FROM_REF, mClass.getFQName());
        MClassRef superRef = new MClassRef(mBase.getFQName());
        mClass.setInheritsFrom(superRef);
        mClass.setModel(mClass.getModel());
    }

    public static MClass getOriginalClazz(Model m, MClass baseClass) {
        final String fqBName = baseClass.getProperty(BASE_FROM_REF);
        if( fqBName==null ) {
            return null;
        }
        return (MClass) ModelHelper.findInStream(m.getModelElements().stream(), (me)->{
            if( me instanceof MClass ) {
                if( ((MClass)me).getFQName().equals(fqBName)) {
                    return true;
                }
            }
            return false;
        });
    }
}
