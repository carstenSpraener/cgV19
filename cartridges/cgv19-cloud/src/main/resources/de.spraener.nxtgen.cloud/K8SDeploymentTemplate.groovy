import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cloud.CloudCartridge
import de.spraener.nxtgen.cloud.CloudStereotypes
import de.spraener.nxtgen.cloud.model.MComponent
import de.spraener.nxtgen.oom.model.MPackage
import de.spraener.nxtgen.oom.model.OOModel

MComponent mComponent = this.getProperty("modelElement");
OOModel model = mComponent.getModel();

def serviceName = mComponent.getName().toLowerCase();
def port = mComponent.getPortList().get(0).getName();
def deploymentName = CloudCartridge.getDeploymentName(mComponent)+"-";


String getSelector(MComponent c) {
    String selector = c.getTaggedValue(CloudStereotypes.CLOUDSERVICE.name, "selector");
    if (selector == null) {
        selector = c.getName().toLowerCase();
    }
    return selector;
}

String getReplicas(MComponent c) {
    String replicas = c.getTaggedValue(CloudStereotypes.CLOUDSERVICE.name, "replicas");
    if (replicas == null) {
        replicas = "1";
    }
    return replicas;
}


String getDockerImage(MComponent c) {
    String dockerImage = c.getName().toLowerCase();
    MPackage module = c.getProvidedCloudModule();
    if (module != null) {
        dockerImage = module.getName().toLowerCase();
    }
    return CloudCartridge.getDeploymentName(c)+"-"+dockerImage;
}

String getDockerRegistry(MComponent c) {
    String registry = ""
    if (c.getParent() == null || c.getParent().getTaggedValue(CloudStereotypes.DEPLOYMENT.name, "dockerRegistry") == null) {
        MPackage module = c.getProvidedCloudModule();
        if (module != null) {
            registry = module.getTaggedValue(CloudStereotypes.CLOUDMODULE.name, "dockerRegistry")
        }
        if (registry == null) {
            registry = "";
        }
    } else {
        registry =  c.getParent().getTaggedValue(CloudStereotypes.DEPLOYMENT.name, "dockerRegistry");
    }
    if (!"".equals(registry) && !registry.endsWith("/")) {
        registry += "/";
    }
    return registry;
}

"""# ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${serviceName}-deployment
spec:
  replicas: ${getReplicas(mComponent)}
  selector:
    matchLabels:
      component: ${getSelector(mComponent)}
  template:
    metadata:
      labels:
        component: ${getSelector(mComponent)}
    spec:
      containers:
        - name: ${serviceName}
          imagePullPolicy: Always
          image: ${getDockerRegistry(mComponent)}${getDockerImage(mComponent)}
          ports:
            - containerPort: ${port}
"""
