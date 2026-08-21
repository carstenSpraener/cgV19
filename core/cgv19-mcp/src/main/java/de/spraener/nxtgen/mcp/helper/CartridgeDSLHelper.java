package de.spraener.nxtgen.mcp.helper;

import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

import java.util.List;

/**
 * Programmatically manipulates a cartridge DSL (an {@link OOModel}) to add
 * stereotypes, transformations and code generators.
 */
public class CartridgeDSLHelper {

    public static final String STEREOTYPE = "Stereotype";
    public static final String TRANSFORMATION = "Transformation";
    public static final String CODE_GENERATOR = "CodeGenerator";
    public static final String CGV19_CARTRIDGE = "cgV19Cartridge";

    /**
     * @return the first top-level package of the model, or null if there is none.
     */
    public MPackage findRootPackage(OOModel model) {
        return model.getChilds().stream()
                .filter(e -> e instanceof MPackage)
                .map(e -> (MPackage) e)
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a class marked with the {@code Stereotype} meta-stereotype.
     */
    public MClass addStereotype(OOModel model, String name, String baseClass, List<TaggedValue> taggedValues) {
        MPackage root = findRootPackage(model);
        MClass clz = root.createMClass(name);
        Stereotype st = new StereotypeImpl(STEREOTYPE);
        if (baseClass != null && !baseClass.isEmpty()) {
            st.setTaggedValue("baseClass", baseClass);
        }
        if (taggedValues != null) {
            for (TaggedValue tv : taggedValues) {
                st.setTaggedValue(tv.getName(), tv.getValue());
            }
        }
        clz.getStereotypes().add(st);
        return clz;
    }

    /**
     * Adds a class marked with the {@code Transformation} meta-stereotype.
     */
    public MClass addTransformation(OOModel model, String name, String pkg, String metaType,
                                    String requiredStereotype, int priority) {
        MPackage target = findOrCreatePackage(model, pkg);
        MClass clz = target.createMClass(name);
        Stereotype st = new StereotypeImpl(TRANSFORMATION);
        st.setTaggedValue("transformedMetaType", metaType);
        st.setTaggedValue("requiredStereotype", requiredStereotype);
        st.setTaggedValue("priority", String.valueOf(priority));
        clz.getStereotypes().add(st);
        return clz;
    }

    /**
     * Adds a class marked with the {@code CodeGenerator} meta-stereotype.
     */
    public MClass addGenerator(OOModel model, String name, String pkg, String requiredStereotype,
                               String outputType, String generatesOn, String outputTo) {
        MPackage target = findOrCreatePackage(model, pkg);
        MClass clz = target.createMClass(name);
        Stereotype st = new StereotypeImpl(CODE_GENERATOR);
        st.setTaggedValue("requiredStereotype", requiredStereotype);
        st.setTaggedValue("outputType", outputType);
        st.setTaggedValue("generatesOn", generatesOn);
        st.setTaggedValue("outputTo", outputTo);
        clz.getStereotypes().add(st);
        return clz;
    }

    /**
     * Finds or creates the package with the given (dotted) name, relative to the root package.
     */
    private MPackage findOrCreatePackage(OOModel model, String pkg) {
        MPackage root = findRootPackage(model);
        if (pkg == null || pkg.isEmpty() || pkg.equals(root.getName())) {
            return root;
        }
        String rootName = root.getName();
        String sub = pkg.startsWith(rootName + ".") ? pkg.substring(rootName.length() + 1) : pkg;
        return findOrCreateSubPackage(root, sub);
    }

    private MPackage findOrCreateSubPackage(MPackage parent, String dottedName) {
        MPackage current = parent;
        for (String part : dottedName.split("\\.")) {
            current = current.findOrCreatePackage(part);
        }
        return current;
    }
}
