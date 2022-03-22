package de.spraener.nxtgen.cartridge.rest.entity;

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

public class EntitySupport {

    public static boolean isLinkAttribute(MAttribute a) {
        return StereotypeHelper.hasStereotye(a, RESTStereotypes.LINK.getName());
    }

    public static MClass findLinkTarget(MAttribute a) {
        if( !isLinkAttribute(a) ) {
            return null;
        }
        String targetName = a.getType();
        MClass targetMC = findMClassByName(a.getModel(), targetName);
        return targetMC;
    }

    public static MClass findMClassByName(Model m, String targetName) {
        return (MClass)
                m.getModelElements().stream()
                .filter(me -> me instanceof MClass)
                .filter(me -> targetName.equals(((MClass)me).getFQName()))
                .findFirst()
                .orElse(null);
    }

    public static MAttribute findLinkOppositeAttribute(MAttribute linkAttr) {
        MClass targetMC = findLinkTarget(linkAttr);
        if( targetMC == null ) {
            return null;
        }
        String type = linkAttr.getType();
        return targetMC.getAttributes()
                .stream()
                .filter(attr -> {
                    return attr.getType().equals(type);
                } )
                .findFirst()
                .orElse(null);
    }

    public static JPALinkClassifier classifyJPALinkType(MAttribute a) {
        if( !StereotypeHelper.hasStereotye(a, RESTStereotypes.LINK.getName()) ) {
            return null;
        }
        boolean isToN = a.isToN();
        MAttribute opposite = findLinkOppositeAttribute(a);
        if( opposite == null ) {
            return null;
        }
        boolean oppositeIsToN = opposite.isToN();
        if( isToN ) {
            if( oppositeIsToN ) {
                return JPALinkClassifier.MANY_TO_MANY;
            } else {
                return JPALinkClassifier.MANY_TO_ONE;
            }
        } else {
            if (oppositeIsToN) {
                return JPALinkClassifier.ONE_TO_MANY;
            } else {
                return JPALinkClassifier.ONE_TO_ONE;
            }
        }
    }

    public static String generateJPALink(MAttribute a) {
        final JPALinkClassifier jpaLinkClass = classifyJPALinkType(a);
        switch (jpaLinkClass) {
            case MANY_TO_MANY:
                return generateManyToMany(a);
            case MANY_TO_ONE:
                return generateManyToOne(a);
            case ONE_TO_MANY:
                return generateOneToMany(a);
            case ONE_TO_ONE:
                return generateOneToOne(a);
        }
        return "";
    }

    private static String generateOneToOne(MAttribute a) {
        return "";
    }

    private static String generateManyToMany(MAttribute a) {
        return "";
    }

    private static String generateManyToOne(MAttribute a) {
        MAttribute opposite = findLinkOppositeAttribute(a);
        StringBuilder sb = new StringBuilder();
        sb.append("@ManyToOne("+
                " fetch = FetchType.LAZY,"+
                ")\n");
        sb.append("@JoinColumn( name = \""+opposite.getName()+"_id\")");
        sb.append("private "+a.getType()+";");
        return sb.toString();
    }

    private static String generateOneToMany(MAttribute a) {
        MAttribute opposite = findLinkOppositeAttribute(a);
        StringBuilder sb = new StringBuilder();
        sb.append("@OneToMany("+
                " mappedBy=\""+opposite.getName()+ "\","+
                " cascade = CascadeType.ALL,"+
                " orphanRemoval=true"+
                ")\n");
        sb.append("private "+a.getType()+" = new ArrayList<>();");
        return sb.toString();
    }
}
