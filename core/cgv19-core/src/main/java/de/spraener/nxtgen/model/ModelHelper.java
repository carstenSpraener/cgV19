package de.spraener.nxtgen.model;

public class ModelHelper {

    public static String getFQName(ModelElement me, String separator ) {
        if( me.getParent()==null || me.getParent() instanceof Model ) {
            return me.getName();
        }
        return getFQName(me.getParent(), separator)+separator+me.getName();
    }

    public static ModelElement findByFQName(Model m, String fqName, String seperator) {
        for( ModelElement child : m.getModelElements() ) {
            if( child.getName().equals(fqName) ) {
                return child;
            }
            if( fqName.startsWith(child.getName()) ) {
                ModelElement target = findByFQName(child, fqName.substring(child.getName().length()+seperator.length()), seperator);
                if( target!=null ) {
                    return target;
                }
            }
        }
        return null;
    }

    public static ModelElement findByFQName(ModelElement parent, String fqName, String seperator) {
        if( fqName.equals(parent.getName()) ) {
            return parent;
        }
        if( fqName.contains(seperator) ) {
            int idx = fqName.indexOf(seperator);
            String childName = fqName.substring(0,idx);
            String tail = fqName.substring(idx+seperator.length());
            ModelElement child = parent.getChilds().stream().filter(
                    e -> e.getName().equals(childName)
            ).findFirst().orElse(null);
            return findByFQName(child, tail, seperator);
        }
        return parent.getChilds().stream().filter(
                c -> c.getName().equals(fqName)
        ).findFirst().orElse(null);
    }
}
