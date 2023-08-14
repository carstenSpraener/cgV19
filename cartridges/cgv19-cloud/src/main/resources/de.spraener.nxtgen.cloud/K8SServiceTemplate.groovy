
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cloud.CloudStereotypes
import de.spraener.nxtgen.cloud.model.MComponent
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MComponent mComponent = this.getProperty("modelElement");

def serviceName = mComponent.getName().toLowerCase();

String getSelector( MComponent c ) {
    String selector = c.getTaggedValue(CloudStereotypes.CLOUDSERVICE.name, "selector");
    if( selector == null ) {
        selector = c.getName().toLowerCase();
    }
    return selector
}

String getPort(MComponent mc) {
    if( mc.getPortList().isEmpty() ) {
        retrun "8080";
    }
    return  mc.getPortList().get(0).getName();
}

"""# ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
apiVersion: v1
kind: Service
metadata:
  name: ${serviceName}
spec:
  type: ClusterIP
  ports:
    - port: ${getPort(mComponent)}
      targetPort: ${getPort(mComponent)}
  selector:
    component: ${getSelector(mComponent)}
"""
