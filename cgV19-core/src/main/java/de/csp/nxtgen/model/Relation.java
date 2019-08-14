package de.csp.nxtgen.model;

public interface Relation {
    String getType();
    void setType( String value);
    String getTargetXmID();
    void setTargetXmID( String value);
    String getTargetType();
    void setTargetType(String targetType);
}
