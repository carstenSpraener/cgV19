package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FSMHelper {
    private MClass fsmClass;
    private MActivity meActivity;
    private Map<String, ModelElement> id2Me = new HashMap<>();

    public FSMHelper(MActivity meActivity) {
        this.meActivity = meActivity;
        setFSMClass((MClass) this.meActivity.getParent());
        fillIDMap(meActivity);
    }

    private void fillIDMap(ModelElement me) {
        if (me.getProperty("id") != null) {
            id2Me.put(me.getProperty("id"), me);
        }
        for (ModelElement child : me.getChilds()) {
            fillIDMap(child);
        }
    }

    public void setFSMClass(MClass fsmClass) {
        this.fsmClass = fsmClass;
    }

    public MClass getFSMClass() {
        return fsmClass;
    }

    public List<ModelElement> findChildsWithMetaType(ModelElement me, String metaType) {
        return me.getChilds().stream()
                .filter((cf) -> cf.getMetaType().equals(metaType))
                .collect(Collectors.toList());
    }

    public List<MActivityControlFlow> findControllFlows(ModelElement me, String idMatchingAttribute) {
        String meID = me.getProperty("id");
        List<MActivityControlFlow> flows = new ArrayList<>();
        for (MActivityControlFlow controllFlow : listControlFlows(me)) {
            String targetID = controllFlow.getProperty(idMatchingAttribute);
            if (targetID.equals(meID)) {
                flows.add(controllFlow);
            }
        }
        return flows;
    }

    private List<MActivityControlFlow> listControlFlows(ModelElement me) {
        final String meID = me.getProperty("id");
        return this.id2Me.values().stream().filter(c -> c instanceof MActivityControlFlow)
                .map(c -> (MActivityControlFlow) c)
                .filter(cf -> cf.getTargetID().equals(meID) || cf.getSourceID().equals(meID))
                .collect(Collectors.toList());
    }

    public List<MActivityControlFlow> findOutgoings(ModelElement me) {
        List<MActivityControlFlow> result = new ArrayList<>();
        for (MActivityControlFlow cf : findControllFlows(me, "sourceID")) {
            ModelElement source = id2Me.get(cf.getTargetID());
            if (source instanceof MActivityDecision) {
                result.addAll(findControllFlows(source, "sourceID"));
            } else {
                result.add(cf);
            }
        }

        return result;
    }

    public List<MActivityControlFlow> findOIncoming(ModelElement me) {
        return findControllFlows(me, "targetID");
    }
}
