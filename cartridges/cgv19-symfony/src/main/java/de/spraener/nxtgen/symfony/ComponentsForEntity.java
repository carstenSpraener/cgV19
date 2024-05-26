package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.cartridge.rest.RESTCartridge;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.php.PhpHelper;
import de.spraener.nxtgen.symfony.php.PhpCodeHelper;

public class ComponentsForEntity extends ComponentsForEntityBase {

    @Override
    public void doTransformationIntern(MClass mc) {
        MPackage pkg = PhpCodeHelper.getProjectRootPackage(mc);
        MPackage componentPkg = pkg.findOrCreatePackage("Component");
        MPackage formPkg = pkg.findOrCreatePackage("Form").findOrCreatePackage("Type");
        MPackage formBasePkg = formPkg.findOrCreatePackage("Base");

        MClass component = createFormTypeComponentClass(formPkg, mc, "Type");
        this.addAllFields(component, mc);
        new GeneratorGapTransformation(()->formBasePkg).doTransformation(component);
        /*
        component = createComponentClass(componentPkg, mc, "LgViewComponent");
        this.addAllFields(component, mc);

        component = createComponentClass(componentPkg, mc, "SmViewComponent");
        this.addNonDetailFieldsOnly(component, mc);

        component = createComponentClass(componentPkg, mc, "RowComponent");
        this.addNonDetailFieldsOnly(component, mc);
        */
    }

    private static MClass createFormTypeComponentClass(MPackage componentPkg, MClass mc, String namePostfix) {
        return OOModelBuilder.createMClass(componentPkg, mc.getName() + namePostfix,
                c -> c.getStereotypes().add(new StereotypeImpl(SymfonyStereotypes.FORMTYPECOMPONENT.getName())),
                c -> GeneratorGapTransformation.setOriginalClass(c, mc)
        );
    }

    private static MClass createComponentClass(MPackage componentPkg, MClass mc, String namePostfix) {
        return OOModelBuilder.createMClass(componentPkg, mc.getName() + namePostfix,
                c -> c.getStereotypes().add(new StereotypeImpl(SymfonyStereotypes.PHPCOMPONENT.getName())),
                c -> c.getStereotypes().add(new StereotypeImpl(SymfonyStereotypes.TWIGCOMPONENT.getName())),
                c -> GeneratorGapTransformation.setOriginalClass(c, mc)
        );
    }

    private void addNonDetailFieldsOnly(MClass component, MClass entity) {
        for( MAttribute a : entity.getAttributes() ) {
            if( StereotypeHelper.hasStereotype(a, RESTStereotypes.PERSISTENTFIELD.getName()) ) {
                String detailOnly = a.getTaggedValue(RESTStereotypes.PERSISTENTFIELD.getName(), "detailOnly");
                if( "true".equals(detailOnly) ) {
                    continue;
                }
            }
            MAttribute cAttr = component.createAttribute(a.getName(), a.getType());
            cAttr.setModel(entity.getModel());
        }
    }

    private void addAllFields(MClass cc, MClass entity) {
        for( MAttribute a : entity.getAttributes() ) {
            MAttribute cAttr = a.cloneTo(cc);
            cAttr.setModel(entity.getModel());
        }
    }
}
