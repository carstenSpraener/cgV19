import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cloud.CloudStereotypes
import de.spraener.nxtgen.model.AbstractModelElement
import de.spraener.nxtgen.model.ModelElement
import de.spraener.nxtgen.model.impl.ModelElementImpl
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

ModelElementImpl me = this.getProperty("modelElement");
String size = me.getTaggedValue(CloudStereotypes.PERMANENTVOLUME.toString(),"size")

"""# ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: ${me.getName().toLowerCase()}-persistent-volume-claim
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: ${size}  
"""
