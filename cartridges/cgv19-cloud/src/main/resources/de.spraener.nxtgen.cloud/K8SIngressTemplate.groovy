import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cloud.CloudStereotypes
import de.spraener.nxtgen.cloud.model.MComponent
import de.spraener.nxtgen.oom.model.MDependency
import de.spraener.nxtgen.oom.model.OOModel

MComponent mComponent = this.getProperty("modelElement");
OOModel model = mComponent.getModel();

String listPaths(MComponent mc) {
    StringBuilder sb = new StringBuilder();
    String routes = CloudStereotypes.ROUTES.name;
    for(MDependency dep : mc.getDependenciesWithStereotype(routes) ) {
        def path = dep.getTaggedValue(routes, "path")
        if( !path.endsWith("/") ) {
            path += "/"
        }
        path += '?(.*)'

        def serviceName = MComponent.getServiceName(dep);
        def port = MComponent.getServicePort(dep);
        sb.append(
"""          - path: ${path}
            pathType: Prefix
            backend:
              service:
                name: ${serviceName}-service
                port:
                  number: ${port}
""");
    }
    return sb.toString();
}
"""# ${ProtectionStrategieDefaultImpl.GENERATED_LINE}
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ingress-service
  annotations:
    nginx.ingress.kubernetes.io/use-regex: 'true'
    nginx.ingress.kubernetes.io/rewrite-target: /\$1
spec:
  ingressClassName: 'nginx'
  rules:
    - http:
        paths:
${listPaths(mComponent)}
"""
