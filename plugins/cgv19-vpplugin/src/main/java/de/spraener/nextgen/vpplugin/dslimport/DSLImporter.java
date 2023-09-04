package de.spraener.nextgen.vpplugin.dslimport;

import com.google.gson.Gson;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import de.spraener.nextgen.vpplugin.CgV19Plugin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class DSLImporter {

    CartridgeDSL dsl = null;

    public DSLImporter(String cartridgeDslJSON) {
        Gson gson = new Gson();
        this.dsl = gson.fromJson(cartridgeDslJSON, CartridgeDSL.class);
    }

    public static final void handleRequest(Context context) {
        try {
            DSLImporter importer = new DSLImporter(context.body());
            importer.doImport();
            context.result("DSL imported.");
        } catch( Exception e ) {
            CgV19Plugin.log(e);
            context.result("Error (see log): "+e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void doImport() {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        IProfile profile = findOrCreateProfile(project, this.dsl.getCartridge());
        for( Stereotype sType : this.dsl.getStereotypes() ) {
            findOrCreate(profile, sType);
        }
    }

    private IStereotype findOrCreate(IProfile profile, Stereotype sType) {
        IStereotype profileSType = null;
        for( IModelElement child : profile.toChildArray() ) {
            if( child instanceof IStereotype && child.getName().equals(sType.getName()) ) {
                profileSType = (IStereotype) child;
            }
        }
        if( profileSType == null ) {
            profileSType = IModelElementFactory.instance().createStereotype();
            profile.addStereotype(profileSType);
            profileSType.setName(sType.getName());
            profileSType.setBaseType(sType.getBaseClass());
        }
        for( TaggedValue tv : sType.getTaggedValues() ) {
            ITaggedValueDefinition modelTV = findOrCreate(profileSType, tv);
            modelTV.setTagDefStereotype(profileSType);
            modelTV.setType(TaggedValue.toTypeID(tv));
            modelTV.setDefaultValue(tv.getDefaultValue());
            if( !tv.getAllowedValues().isEmpty() ) {
                String[] enumValues = tv.getAllowedValues().toArray(new String[tv.getAllowedValues().size()]);
                modelTV.setType(2);
                modelTV.setEnumerationValues(enumValues);
            }
        }
        return profileSType;
    }

    private ITaggedValueDefinition findOrCreate(IStereotype profileSType, TaggedValue tv) {
        if( profileSType.getTaggedValueDefinitions() != null ) {
            for (ITaggedValueDefinition child : profileSType.getTaggedValueDefinitions().toTaggedValueDefinitionArray()) {
                if (child.getName().equals(tv.getName())) {
                    return child;
                }
            }
        }
        ITaggedValueDefinition result = IModelElementFactory.instance().createTaggedValueDefinition();
        result.setName(tv.getName());
        if( profileSType.getTaggedValueDefinitions() == null ) {
            ITaggedValueDefinitionContainer tvContainer  = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
            profileSType.setTaggedValueDefinitions(tvContainer);
        }
        profileSType.getTaggedValueDefinitions().addTaggedValueDefinition(result);
        return result;
    }

    private IProfile findOrCreateProfile(IProject project, String cartridge) {
        IProfile profile = findProfile(project, cartridge);

        if( profile == null ) {
            profile = IModelElementFactory.instance().createProfile();
            profile.setName(cartridge);
        }
        getRootModel(project).addChild(profile);
        return profile;
    }

    private IModel getRootModel(IProject project) {
        for( IModelElement me : project.toModelElementArray() ) {
            if( me instanceof IModel ) {
                return (IModel) me;
            }
        }
        IModel model = IModelElementFactory.instance().createModel();
        model.setName("ImportedDSLs");
        return model;
    }

    private IProfile findProfile(IProject project, String cartridge) {
        for (IModelElement mElement : project.toModelElementArray()) {
            if( mElement instanceof IProfile && mElement.getName().equals(cartridge) ) {
                return (IProfile) mElement;
            }
        }
        return null;
    }
}
