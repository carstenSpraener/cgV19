package de.spraener.nextgen.vpplugin.cartridge;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class GeneratorGapTransformation extends GeneratorGapTransformationBase {
    public static final String BASE_FROM_REF = "GeneratorGapBaseFromProperty";

    @Override
    public void doTransformationIntern(MClass me) {
        if( !StereotypeHelper.hasStereotye(me, "Exporter") ) {
            return;
        }
        System.out.println("Running Transformation on "+me.getFQName());
        MGeneratorGapBaseClass mBase = new MGeneratorGapBaseClass(me);
        mBase.setParent(me.getParent());
        mBase.setName(me.getName()+"Base");
        mBase.setInheritsFrom(me.getInheritsFrom());
        for(Stereotype sType : me.getStereotypes() ) {
            Stereotype stBase = new StereotypeImpl(sType.getName()+"Base");
            mBase.getStereotypes().add(stBase);
        }
        mBase.setProperty(BASE_FROM_REF, me.getFQName());
        MClassRef superRef = new MClassRef(mBase.getFQName());
        me.setInheritsFrom(superRef);
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
