package de.spraener.nextgen.vpplugin.dslimport;

import java.util.ArrayList;
import java.util.List;

import static de.spraener.nextgen.vpplugin.oom.StereotypeExporter.TAGGED_VALUE_TYPE;

public class TaggedValue {
    private String name;
    private String type;
    private String typeID;
    private String defaultValue = "null";

    private List<String> allowedValues = new ArrayList<>();

    public static int toTypeID(TaggedValue tv) {
        for( int i=0; i< TAGGED_VALUE_TYPE.length; i++ ) {
            if( TAGGED_VALUE_TYPE[i].equals(tv.getType())) {
                return i;
            }
        }
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
