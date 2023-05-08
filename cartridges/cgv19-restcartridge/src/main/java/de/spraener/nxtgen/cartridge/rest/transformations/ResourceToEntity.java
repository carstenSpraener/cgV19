package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;

import java.util.HashSet;
import java.util.Set;

public class ResourceToEntity extends ResourceToEntityBase {
    private static final Set<String> TABLENAME_KEYWORDS = new HashSet<>();

    static {
        TABLENAME_KEYWORDS.add("USER");
    }

    public static void ensureEntityDefinition(MClass mClass) {
        Stereotype sType = StereotypeHelper.getStereotype(mClass, RESTStereotypes.ENTITY.getName());
        String tableName = sType.getTaggedValue("dbTable");
        if (tableName == null) {
            sType.setTaggedValue("dbTable", toTableName(mClass));
        } else {
            sType.setTaggedValue("dbTable", tableName);
        }
        ensureHasId(mClass);
        for( MAttribute a : mClass.getAttributes() ) {
            ensureEntityDefinition(a);
        }
        for( MAssociation a : mClass.getAssociations() ) {
            convertToEntityType(a);
        }
        ensureSuperClassIsEntity(mClass);
    }

    private static void ensureSuperClassIsEntity(MClass mClass) {
        if( mClass.getInheritsFrom()!=null ) {
            MClass superClass = mClass.getInheritsFrom().getMClass(mClass.getModel());
            if( superClass==null ) {
                NextGen.LOGGER.severe("InheritsFrom in Clas "+mClass.getName()+" points to "+mClass.getInheritsFrom().getFullQualifiedClassName()+
                " and is not resolveable");
            }
            if( !superClass.hasStereotype(RESTStereotypes.ENTITY.getName()) ) {
                String entitySuperClasName = superClass.getParent().getFQName()+".model."+superClass.getName();
                mClass.setInheritsFrom(new MClassRef(entitySuperClasName));
            }
        }
    }

    public static void ensureEntityDefinition(MAttribute a) {
        if( !StereotypeHelper.hasStereotye(a, RESTStereotypes.DBFIELD.getName())) {
            Stereotype sType = new StereotypeImpl(RESTStereotypes.DBFIELD.getName());
            sType.setTaggedValue("dbFieldName", toDbFieldName(a.getName()));
            sType.setTaggedValue("dbType", toDbType(a.getType()));
            a.getStereotypes().add(sType);
        }
    }

    @Override
    public void doTransformationIntern(MClass element) {
        create((MClass)element);
    }

    public MClass create(MClass mClass) {
        // Create new ModelClass
        MPackage pkg = mClass.getPackage();
        MPackage entityPkg = pkg.findOrCreatePackage("model");
        MClass entity = entityPkg.createMClass(mClass.getName());

        // Set Stereotype
        Stereotype eSType = new StereotypeImpl(RESTStereotypes.ENTITY.getName());
        eSType.setTaggedValue("dbTable", toTableName(mClass));
        entity.getStereotypes().add(eSType);
        // copy attributes as DBFields
        for (MAttribute attr : mClass.getAttributes()) {
            if( StereotypeHelper.hasStereotye(attr, RESTStereotypes.LINK.getName()) ) {
                 entity.addChilds(createLinkRelation(attr));
            } else {
                entity.addAttribute(toDBField(attr));
            }
        }
        OOModel ooModel = (OOModel)mClass.getModel();
        for(MAssociation assoc : mClass.getAssociations() ) {
            MAssociation eAssoc = createFrom(assoc);
            convertToEntityType(eAssoc);
            entity.getAssociations().add(eAssoc);
        }
        ensureHasId(entity);

        // Create the Repository to access the Entity from the DB
        MClass repo = entityPkg.createMClass(mClass.getName() + "Repository");
        Stereotype repoSType = new StereotypeImpl(RESTStereotypes.REPOSITORY.getName());
        repo.addStereotypes(repoSType);
        repoSType.setTaggedValue("keyType", "Long");
        repoSType.setTaggedValue("dataType", entity.getFQName());
        StringBuffer attrList = new StringBuffer();
        for( MAttribute attr : mClass.getAttributes() ) {
            if( attrList.length()>0 ) {
                attrList.append(";");
            }
            attrList.append(attr.getName());
        }
        repoSType.setTaggedValue("attrList", attrList.toString());
        return entity;
    }

    private MAssociation createFrom(MAssociation assoc) {
        MAssociation result = new MAssociation();
        result.setModel(assoc.getModel());
        result.setAssociationType(assoc.getAssociationType());
        result.setAssocId (assoc.getAssocId());
        result.setComposite (assoc.getComposite());
        result.setMultiplicity (assoc.getMultiplicity());
        result.setOpositeAttribute (assoc.getOpositeAttribute());
        result.setOpositeMultiplicity (assoc.getOpositeMultiplicity());
        result.setType (assoc.getType());
        result.setName (assoc.getName());

        return result;
    }

    private static void convertToEntityType(MAssociation eAssoc) {
        String targetType = eAssoc.getType();
        if( targetType.contains("model") ) {
            return;
        }
        String targetPkg = targetType.substring(0,targetType.lastIndexOf('.'));
        String targetClass = targetType.substring(targetType.lastIndexOf('.')+1);
        if( !targetPkg.endsWith("model")) {
            targetPkg = targetPkg+".model";
        }
        eAssoc.setType(targetPkg+"."+targetClass);
    }

    private ModelElement createLinkRelation(MAttribute attr) {
        ModelElement e = new ModelElementImpl();
        Stereotype st = new StereotypeImpl(RESTStereotypes.RSRCLINK.getName());
        e.getStereotypes().add(st);
        st.setTaggedValue("target", attr.getType());
        st.setTaggedValue("cardinality", attr.isToN() ? "*" : "1");
        return e;
    }

    public static void ensureHasId(MClass entity) {
        MAttribute aId = null;
        for (MAttribute a : entity.getAttributes()) {
            if (a.getName().equals("id")) {
                aId = a;
                break;
            }
        }
        if (aId == null) {
            aId = entity.createAttribute("id", "Long");
        }
        Stereotype sType = StereotypeHelper.getStereotype(aId, RESTStereotypes.DBFIELD.getName());
        if (sType == null) {
            sType = new StereotypeImpl(RESTStereotypes.DBFIELD.getName());
            sType.setTaggedValue("dbFieldName", toDbFieldName(aId.getName()));
            sType.setTaggedValue("dbType", toDbType(aId.getType()));
            aId.getStereotypes().add(sType);
        }
        sType.setTaggedValue("isKey", "true");
    }

    private MAttribute toDBField(MAttribute attr) {
        MAttribute dbField = new MAttribute(attr);
        Stereotype sType = new StereotypeImpl(RESTStereotypes.DBFIELD.getName());
        sType.setTaggedValue("dbFieldName", toDbFieldName(attr.getName()));
        sType.setTaggedValue("dbType", toDbType(attr.getType()));
        dbField.getStereotypes().add(sType);
        return dbField;
    }

    public static String toTableName(MClass mClass) {
        String tableName = mClass.getTaggedValue(RESTStereotypes.ENTITY.getName(), "dbTableName");
        if( tableName==null) {
            tableName = camelCaseToDbName(mClass.getName());
            if (TABLENAME_KEYWORDS.contains(tableName)) {
                tableName = tableName + "_TAB";
            }
        }
        return tableName;
    }

    public static String toDbFieldName(String name) {
        return camelCaseToDbName(name);
    }

    public static String toDbType(String type) {
        String dbType = "VARCHAR(256)";
        if( "Long".equals(type) || "java.lang.Long".equals(type)) {
            dbType =  "BIGINT";
        }
        if( "Date".equals(type) || "java.util.Date".equals(type)) {
            dbType =  "DATE";
        }
        if( "Boolean".equals(type) || "java.lang.Boolean".equals(type)) {
            dbType =  "INTEGER";
        }
        if( "Double".equals(type) || "java.lang.Double".equals(type)) {
            dbType =  "DECIMAL(15,5)";
        }
        return dbType;
    }

    public static String camelCaseToDbName(String name) {
        StringBuilder sb = new StringBuilder();
        char prevChar = 0;
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c) && Character.isLowerCase(prevChar)) {
                sb.append('_');
            }
            sb.append(Character.toUpperCase(c));
            prevChar = c;
        }
        return sb.toString();
    }
}
