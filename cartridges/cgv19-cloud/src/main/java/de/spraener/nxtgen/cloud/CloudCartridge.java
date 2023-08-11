package de.spraener.nxtgen.cloud;


import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.cartridges.AnnotatedCartridgeImpl;
import de.spraener.nxtgen.cartridges.GeneratorWrapper;
import de.spraener.nxtgen.cloud.model.MComponent;
import de.spraener.nxtgen.filestrategies.GeneralFileStrategy;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@CGV19Cartridge("CloudCartridge")
public class CloudCartridge extends CloudCartridgeBase{
    public static final String NAME = "CloudCartridge";
    private AnnotatedCartridgeImpl annotatedCartridgeContent = new AnnotatedCartridgeImpl(CloudCartridge.class);
    private static MPackage deploymentPackage = null;

    public CloudCartridge() {
        super();
    }

    public static List<MPackage> findCloudModules(Model model) {
        List<MPackage> result = new ArrayList<>();
        for( ModelElement e : model.getModelElements() ) {
            if( e instanceof MPackage pkg && StereotypeHelper.hasStereotype(pkg ,CloudStereotypes.CLOUDMODULE.getName()) ) {
                result.add(pkg);
            }
        }
        return result;
    }

    public static String getDeploymentRegistry(ModelElement modelElement) {
        MPackage pkg = getDeploymentPackage(modelElement.getModel());
        if( pkg == null ) {
            return null;
        }
        return pkg.getTaggedValue(
                CloudStereotypes.DEPLOYMENT.getName(), "dockerRegistry"
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> transformations = annotatedCartridgeContent.getTransformations();
        transformations.addAll(super.getTransformations());
        return transformations;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> mappings = this.annotatedCartridgeContent.mapGenerators(m);
        mappings.addAll(super.mapGenerators(m));
        return mappings;
    }

    @Override
    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName) {
        if( me instanceof MComponent mc ) {
            if(stereotypeName.equals(CloudStereotypes.CLOUDCLUSTERIPSERVICE.getName())) {
                return CodeGeneratorMapping.create(me, new K8SServiceGenerator(
                        cb -> cb.setToFileStrategy(new K8SConfigFileSpec(me, "-service"))
                ));
            }
            if(stereotypeName.equals(CloudStereotypes.CLOUDDEPLOYABLE.getName())) {
                return CodeGeneratorMapping.create(me, new K8SDeploymentGenerator(
                        cb -> cb.setToFileStrategy(new K8SConfigFileSpec(me, "-deployment"))
                ));
            }
        }
        if( me instanceof MClass mc && stereotypeName.equals("GradleSettings")) {
            return CodeGeneratorMapping.create(mc, new GradleSettingsGenerator(
                    cb -> cb.setToFileStrategy(new GeneralFileStrategy(NextGen.getWorkingDir(),"settings","gradle"))
            ));
        }
        if( me instanceof MClass mc && stereotypeName.equals(CloudStereotypes.DOCKERCOMPOSEFILE.getName())) {
            return CodeGeneratorMapping.create(mc, new DockerComposeGenerator(
                    cb -> cb.setToFileStrategy(new GeneralFileStrategy(NextGen.getWorkingDir(),"docker-compose","yml"))
            ));
        }
        if( me instanceof MComponent mc && stereotypeName.equals(CloudStereotypes.CLOUDINGRESSSERVICE.getName())) {
            return CodeGeneratorMapping.create(mc, new K8SIngressGenerator(
                    cb -> cb.setToFileStrategy(new K8SConfigFileSpec(mc,"-service"))
            ));
        }
        return super.createMapping(me, stereotypeName);
    }

    public static MPackage getDeploymentPackage(Model m) {
        if( deploymentPackage==null ) {
            for( ModelElement e : m.getModelElements() ) {
                if( e instanceof MPackage pkg && StereotypeHelper.hasStereotype(pkg, CloudStereotypes.DEPLOYMENT.getName()) ) {
                    deploymentPackage = pkg;
                    break;
                }
            }
        }
        return deploymentPackage;
    }

    public static String getDeploymentName(ModelElement me) {
        if( me instanceof MPackage pkg && StereotypeHelper.hasStereotype(pkg, CloudStereotypes.DEPLOYMENT.getName()) ) {
            String name = pkg.getTaggedValue(CloudStereotypes.DEPLOYMENT.getName(), "deploymentName");
            if( name == null ) {
                if( pkg.getParent() != null ) {
                    name = pkg.getParent().getName();
                } else {
                    name = pkg.getName().toLowerCase();
                }
            }
            int dotIdx = name.indexOf('.');
            if( dotIdx >= 0 ) {
                name = name.substring(name.lastIndexOf('.')+1);
            }
            return name;
        }
        if( me.getParent() != null ) {
            return getDeploymentName(me.getParent());
        } else {
            return "undefined";
        }
    }
}
