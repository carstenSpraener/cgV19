package de.csp.codgen.oom.cartridge;

import de.csp.codegen.model.oom.MAttribute;
import de.csp.nxtgen.model.ModelElement;
import de.csp.nxtgen.model.Relation;

public class JavaHelper {

    public static String printAttributeDefinition(MAttribute a) {
        return "    "+a.getProperty("visibility")+" "+a.getType()+" "+a.getName()+";";
    }


    public static String getExtendsStr(ModelElement me) {
        return relationList(me, "extends");
    }

    public static String getImplementsStr(ModelElement me) {
        return relationList(me, "implements");
    }

    public static String relationList( ModelElement me, String relType ) {
        String relStr = "";
        for(Relation r  :me.getRelations() ) {
            if( r.getType().equals(relType) ) {
                if( relStr.isEmpty() ) {
                    relStr = " "+relType+" ";
                } else {
                    relStr += ", ";
                }
                relStr +=r.getTargetType();
            }
        }
        return relStr;

    }

    public static String printAttributeAccessMethods(MAttribute attr) {
        StringBuilder sb = new StringBuilder();
        String type = attr.getProperty("type");
        String visibility = attr.getProperty("visibility");
        String multiplicity = attr.getProperty("multiplicity");
        String methodName = attr.getName().substring(0,1).toUpperCase()+attr.getName().substring(1);
        if( attr.isToN() ) {
            String lType = "List<"+type+">";
            sb.append("    public "+lType+" get"+methodName+"() {\n");
            sb.append("        return this."+attr.getName()+";\n");
            sb.append("    }\n");
            sb.append("\n");

            sb.append("    public void set"+methodName+"( "+lType+" value) {\n");
            sb.append("        this."+attr.getName()+" = value;\n");
            sb.append("    }\n");
            sb.append("\n");

            sb.append("    public "+type+" add"+methodName+"( "+type+" value) {\n");
            sb.append("        this."+attr.getName()+".add(value);\n");
            sb.append("        return value;\n");
            sb.append("    }\n");
            sb.append("\n");

            sb.append("    public "+type+" remove"+methodName+"( "+type+" value) {\n");
            sb.append("        this."+attr.getName()+".remove(value);\n");
            sb.append("        return value;\n");
            sb.append("    }\n");
            sb.append("");

            sb.append("    public boolean contains"+methodName+"( "+type+" value) {\n");
            sb.append("        return this."+attr.getName()+".contains(value);\n");
            sb.append("    }\n");
            sb.append("\n");

        } else {
            sb.append("    public "+type+" get"+methodName+"() {");
            sb.append("        return this."+attr.getName()+";");
            sb.append("    }");
            sb.append("");

            sb.append("    public void set"+methodName+"( "+type+" value) {");
            sb.append("        this."+attr.getName()+" = value;");
            sb.append("    }");
            sb.append("");
        }
        return sb.toString();
    }
}
