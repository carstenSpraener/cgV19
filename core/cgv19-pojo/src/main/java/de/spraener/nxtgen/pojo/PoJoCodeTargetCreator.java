package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeTarget;

public class PoJoCodeTargetCreator {
    public static final String POJO_ASPECT = "pojo-frame";
    public static final String ATTRIBUTE_ASPECT = "pojo-attribute";
    public static final String ASSOCIATION = "pojo-association";
    private MClass mClass;

    public PoJoCodeTargetCreator(MClass mClass) {
        this.mClass = mClass;
    }

    public CodeTarget createPoJoTarget() {
        CodeTarget target = new ClassFrameTargetCreator(this.mClass).createPoJoTarget();
        MClass orgClass = PoJoGenerator.getOriginalClass(this.mClass);
        if( orgClass == null ) {
            orgClass = mClass;
        }
        if( orgClass!=null ) {
            final MClass pojo = orgClass;
            target.inContext(POJO_ASPECT, pojo,
                    ct -> new PoJoAttributesCreator().accept(ct, pojo),
                    ct -> new PoJoAssociationCreator().accept(ct, pojo)
            );
        }
        return target;
    }

}
