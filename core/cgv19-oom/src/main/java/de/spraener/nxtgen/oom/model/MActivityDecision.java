package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MActivityDecision extends MAbstractModelElement {
    private String id;
    private List<MActivityControlFlow> incoming;
    private List<MActivityControlFlow> outgoing;

    public MActivityDecision() {
    }

    public void postDefinition() {
        super.postDefinition();
        ModelElement meIncoming = ModelHelper.findInStream(getChilds().stream(), child -> {
            return child.getMetaType().equals("incoming");
        });
        this.incoming = ((ModelElementImpl)meIncoming).filterChilds( child -> child instanceof MActivityControlFlow)
                .map(meCF-> (MActivityControlFlow)meCF)
                .collect(Collectors.toList());
        if( incoming==null ) {
            incoming = new ArrayList<>();
        }

        ModelElement meOutgoing = ModelHelper.findInStream(getChilds().stream(), child -> {
            return child.getMetaType().equals("outgoing");
        });
        this.outgoing =  ((ModelElementImpl)meIncoming).filterChilds( child -> child instanceof MActivityControlFlow)
                .map(meCF-> (MActivityControlFlow)meCF)
                .collect(Collectors.toList());
        if( outgoing==null ) {
            outgoing = new ArrayList<>();
        }
    }

    public MActivityControlFlow createOutgoingControlFlow(MAbstractModelElement target) {
        MActivityControlFlow outgoing = new MActivityControlFlow();
        outgoing.setParent(this);
        outgoing.setModel(getModel());
        outgoing.setSource(getName());
        outgoing.setSourceID(getXmiID());
        outgoing.setTargetID(target.getXmiID());
        outgoing.setTarget(target.getName());
        if( this.outgoing == null ) {
            this.outgoing = new ArrayList<>();
        }
        this.outgoing.add(outgoing);
        getChilds().add(outgoing);

        return outgoing;
    }

    public MActivityControlFlow createIncomingControlFlow(MAbstractModelElement source) {
        MActivityControlFlow incoming = new MActivityControlFlow();
        incoming.setParent(this);
        incoming.setModel(getModel());
        incoming.setSource(source.getName());
        incoming.setSourceID(source.getXmiID());
        incoming.setTargetID(getXmiID());
        incoming.setTarget(getName());
        if( this.incoming == null ) {
            this.incoming = new ArrayList<>();
        }
        this.incoming.add(incoming);
        getChilds().add(incoming);

        return incoming;
    }
}
