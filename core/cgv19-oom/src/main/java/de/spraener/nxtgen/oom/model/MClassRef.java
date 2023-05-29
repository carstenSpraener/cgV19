package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.Model;

import java.io.Serializable;

public class MClassRef implements Serializable {
    private String fullQualifiedClassName;

    public MClassRef() {
    }

    public MClassRef(String fullQualifiedClassName) {
        this.fullQualifiedClassName = fullQualifiedClassName;
    }

    public MClass getMClass(Model m) {
        return ((OOModel)m).findClassByName(this.fullQualifiedClassName);
    }

    public MClassRef cloneTo(MClass target) {
        MClassRef tRef = new MClassRef(this.fullQualifiedClassName);

        return tRef;
    }

    public String getFullQualifiedClassName() {
        return fullQualifiedClassName;
    }

}
