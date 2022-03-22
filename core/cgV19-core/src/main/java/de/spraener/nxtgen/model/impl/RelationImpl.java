package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.Relation;

public class RelationImpl implements Relation {
    String type;
    String targetXmID;
    String targetType;

    public RelationImpl() {
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getTargetXmID() {
        return targetXmID;
    }

    @Override
    public void setTargetXmID(String targetXmID) {
        this.targetXmID = targetXmID;
    }

    @Override
    public String getTargetType() {
        return targetType;
    }

    @Override
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
