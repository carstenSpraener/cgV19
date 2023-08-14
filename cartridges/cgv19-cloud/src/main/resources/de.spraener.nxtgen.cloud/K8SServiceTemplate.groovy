
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cloud.CloudStereotypes
import de.spraener.nxtgen.cloud.model.MComponent
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MComponent mComponent = this.getProperty("modelElement");
OOModel model = mComponent.getModel();

def serviceName = mComponent.getName().toLowerCase();
def port = mComponent.getPortList().get(0).getName();

String getSelector( MComponent c ) {
    String selector = c.getTaggedValue(CloudStereotypes.CLOUDSERVICE.name, "selector");
    if( selector == null ) {
        selector = c.getName().toLowerCase();
    }
}

"""# ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
apiVersion: v1
kind: Service
metadata:
  name: ${serviceName}
spec:
  type: ClusterIP
  ports:
    - port: ${port}
      targetPort: ${port}
  selector:
    component: ${getSelector(mComponent)}
"""
