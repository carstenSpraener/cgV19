package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;

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

        ModelElement meOutgoing = ModelHelper.findInStream(getChilds().stream(), child -> {
            return child.getMetaType().equals("outgoing");
        });
        this.outgoing =  ((ModelElementImpl)meIncoming).filterChilds( child -> child instanceof MActivityControlFlow)
                .map(meCF-> (MActivityControlFlow)meCF)
                .collect(Collectors.toList());
    }
}
