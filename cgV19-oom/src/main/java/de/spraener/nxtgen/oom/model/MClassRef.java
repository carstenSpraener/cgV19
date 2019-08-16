package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.Model;

public class MClassRef {
    private String fullQualifiedClassName;

    public MClassRef(String fullQualifiedClassName) {
        this.fullQualifiedClassName = fullQualifiedClassName;
    }

    public MClass getMClass(Model m) {
        return ((OOModel)m).findClassByName(this.fullQualifiedClassName);
    }
}
