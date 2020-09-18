package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;

import java.util.List;
import java.util.stream.Collectors;

public class MActivityDecision extends ModelElementImpl {
    private String id;
    private List<MActivityControlFlow> incoming;
    private List<MActivityControlFlow> outgoing;

    public MActivityDecision(ModelElement me) {
        OOModelHelper.mapProperties(this, this.getClass(), me);

        ModelElement meIncoming = ModelHelper.findInStream(me.getChilds().stream(), child -> {
            return child.getMetaType().equals("incoming");
        });
        this.incoming =  ((ModelElementImpl)meIncoming).filterChilds( child -> {
            return child.getMetaType().equals("mControlFlow");
        }).map(meCF-> {
            MActivityControlFlow cf = (MActivityControlFlow)OOModelRepository.getInstance().get(meCF.getProperty("id"));
            if( cf == null ) {
                cf = new MActivityControlFlow(meCF);
                cf.setParent(this);
            }
            return cf;
        }).collect(Collectors.toList());
        super.getChilds().addAll(this.incoming);

        ModelElement meOutgoing = ModelHelper.findInStream(me.getChilds().stream(), child -> {
            return child.getMetaType().equals("outgoing");
        });
        this.outgoing =  ((ModelElementImpl)meOutgoing).filterChilds( child -> {
            return child.getMetaType().equals("mControlFlow");
        }).map(meCF-> {
            MActivityControlFlow cf = (MActivityControlFlow)OOModelRepository.getInstance().get(meCF.getProperty("id"));
            if( cf == null ) {
                cf = new MActivityControlFlow(meCF);
                cf.setParent(this);
            }
            return cf;
        }).collect(Collectors.toList());
        super.getChilds().addAll(this.outgoing);

    }
}
