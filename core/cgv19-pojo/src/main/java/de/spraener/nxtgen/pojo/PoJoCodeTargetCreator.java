package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.cartridges.EvaluationRequest;
import de.spraener.nxtgen.oom.model.MActivity;
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
            target.forAspect(POJO_ASPECT, pojo,
                    ct -> new PoJoAttributesCreator().accept(ct, pojo),
                    ct -> new PoJoAssociationCreator().accept(ct, pojo),
                    ct -> {
                        // if the pojo has any activities try to resolve them with another cartridge
                        // that supports activity generation.
                        for(MActivity activity : pojo.getActivities() ) {
                            EvaluationRequest activityRequest = new EvaluationRequest(
                                    activity,
                                    PoJoCartridge.POJO_STEREOTYPE,
                                    PoJoCartridge.ACTIVITY_ASPECT,
                                    ct);
                            NextGen.evaluateByAny(activityRequest);
                        }
                    }
            );
        }
        return target;
    }

}
