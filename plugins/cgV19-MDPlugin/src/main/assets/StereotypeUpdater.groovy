import com.nomagic.magicdraw.core.Application
import com.nomagic.magicdraw.core.Project
import com.nomagic.magicdraw.uml.Finder
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile
import  com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype
import de.spraener.nxtgen.model.ModelElement
import de.spraener.nxtgen.model.TaggedValue

magicDrawElement = (Element)this.getProperty("magicDrawElement");
modelElement = (ModelElement)this.getProperty("modelElement");
mdProject = (Project)Application.getInstance().getProject();

def applyStereotype(Element e, String stereotypeName) {
    Stereotype st = Finder.byNameRecursively().find(mdProject,Stereotype.class, stereotypeName)
    if( st==null ) {
        st = mdProject.getElementsFactory().createStereotypeInstance();
        st.setName(stereotypeName);
        st.setOwner(getProfile());
    }
    StereotypesHelper.addStereotype(e, st);
    return st;
}

void applyTaggedValue(Stereotype stereotype, TaggedValue taggedValue) {
    Application.getInstance().getGUILog().writeLogText("Adding Tagged "+taggedValue.name+" value "+taggedValue.value+" to element "+magicDrawElement.getName(), true)
    if( Finder.byName().find(stereotype, Property.class, taggedValue.name)==null) {
        Property p = mdProject.getElementsFactory().createPropertyInstance();
        p.setName(taggedValue.getName());
        p.setOwner(stereotype);
    }
    StereotypesHelper.setStereotypePropertyValue(magicDrawElement, stereotype, taggedValue.name, taggedValue.value);
}

for(de.spraener.nxtgen.model.Stereotype stereotype: modelElement.stereotypes ) {
    Stereotype mdStereotype = applyStereotype(magicDrawElement, stereotype.getName());
    for(TaggedValue tv : stereotype.taggedValues) {
        applyTaggedValue(mdStereotype, tv)
    }
}

Package findOrCreatePakage(String fqPkgName) {
    Package parent = this.project.getPrimaryModel();
    for( String pkgName : fqPkgName.split("\\.") ) {
        Package pkg = Finder.byName().find(parent, Package.class, pkgName);
        if( pkg == null ) {
            pkg = mdProject.getElementsFactory().createPackageInstance();
            pkg.setOwner(parent);
            pkg.setName(pkgName);
        }
        parent = pkg;
    }

    return parent;
}

Profile getProfile() {
    profile = Finder.byName().find(mdProject.getPrimaryModel(), Profile.class, "Dsl")
    if( profile == null ) {
        profile = mdProject.getElementsFactory().createProfileInstance();
        profile.setName("Dsl");
        profile.setOwner(mdProject.getPrimaryModel());
    }
    return profile
}
