package de.spraener.nxtgen.model;

public interface Relation {
    String getType();
    void setType( String value);
    String getTargetXmID();
    void setTargetXmiID( String value);
    String getTargetType();
    void setTargetType(String targetType);
}
