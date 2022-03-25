import de.csp.demo.rest.model.Adresse;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.impl.ModelImpl;
import de.spraener.nxtgen.oom.model.MPackage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImportClass {
    Set<String> importedClasses = new HashSet<>();

    @Test
    public void test() throws Exception {
        ImportClass.main(new String[]{});
    }

    public static void main(String... args) throws Exception {
        String className = MPackage.class.getName();
        if( args.length>0 ) {
            className = args[0];
        }
        ImportClass imports = new ImportClass();
        String payload = imports.toPayload(className);
        System.out.println(payload);
        HttpClient http = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:7000");
        post.addHeader("XX-CGV19-TYPE", "oom");
        StringEntity body = new StringEntity(payload);
        body.setContentType(ContentType.APPLICATION_JSON.toString());
        post.setEntity(body);
        HttpResponse response = http.execute(post);
        System.out.println(response.getStatusLine());
    }

    private String toPayload(String cName) throws ClassNotFoundException {
        Class c = Class.forName(cName);
        String className = c.getName();
        String pkg = className.substring(0, className.lastIndexOf('.'));
        className = c.getSimpleName();
        StringBuffer sb = new StringBuffer();
        sb.append("import de.spraener.nxtgen.groovy.ModelDSL\n" +
                "\n" +
                "ModelDSL.make {\n");
        sb.append("    mPackage {\n");
        sb.append("      name '" + pkg + "'\n");
        toPayload(sb, c);
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }

    private void toPayload(StringBuffer sb, Class c) {
        List<Class<?>> subClasses = new ArrayList<>();
        importedClasses.add(c.getName());
        String pkgName = c.getName().substring(0, c.getName().lastIndexOf('.'));
        sb.append("      mClass {\n");
        sb.append("        name '" + c.getSimpleName() + "'\n");
        for (Field f : c.getDeclaredFields()) {
            toPayload(sb, f);
            if( f.getType().getName().startsWith(pkgName)) {
                if( !importedClasses.contains(f.getType().getName()) ) {
                    subClasses.add(f.getType());
                }
            }
        }
        for( Method m : c.getDeclaredMethods() ) {
            if( m.getName().contains("$")) {
                // Lambda-Methods or similar filterd.
                continue;
            }
            toPayload(sb, m);
            if( m.getReturnType().getName().startsWith(pkgName)) {
                if( !importedClasses.contains(m.getReturnType().getName()) ) {
                    subClasses.add(m.getReturnType());
                }
            }
        }
        sb.append("      }\n");
        for( Class subC : subClasses ) {
            toPayload(sb, subC);
        }
    }

    private void toPayload(StringBuffer sb, Method m) {
        sb.append("        mOperation {\n");
        sb.append("          name '"+m.getName()+"'\n");
        sb.append("          type '"+m.getReturnType().getName()+"'\n" +
                "          visibility '"+getAccess(m)+"'\n");
        for(Parameter p : m.getParameters()) {
            toPayload(sb,p);
        }
        sb.append("        }\n");

    }

    private void toPayload(StringBuffer sb, Parameter p) {
        sb.append("          mParameter {\n");
        sb.append("            name '"+p.getName()+"'\n");
        sb.append("            type '"+p.getType().getName()+"'\n");
        sb.append("          }\n");
    }

    private String getAccess(Method m) {
        return m.isAccessible() ? "public" : "private";
    }

    private void toPayload(StringBuffer sb, Field f) {
        sb.append("        mAttribute {\n");
        sb.append("          name '"+f.getName()+"'\n");
        sb.append("          type '"+f.getType().getName()+"'\n" +
                "          visibility '"+getAccess(f)+"'\n" +
                "        }\n");
    }

    private String getAccess(Field f) {
        return f.isAccessible() ? "publiv" : "private";
    }
}
