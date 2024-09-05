package de.spraener.cgv19.webtest;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.File;

public class HtmlModelReader implements ModelLoader {

    @Override
    public boolean canHandle(String modelURI) {
        File f = new File(modelURI);
        return f.exists() && f.isDirectory();
    }

    @Override
    public Model loadModel(String modelURI) {
        File modelDir = new File(modelURI);
        OOModel model = new OOModel();
        // Create the root package
        MPackage root = new MPackage();
        root.setName("de.spraener.webtest");
        root.setModel(model);
        model.addModelElement(root);
        readModelDir(modelDir, root);

        return model;
    }

    private void readModelDir(File modelDir, MPackage root) {
        for (File f : modelDir.listFiles()) {
            MPackage pkg = root.findOrCreatePackage(f.getName().toLowerCase());
            pkg.setModel(root.getModel());
            if (f.isDirectory()) {
                readModelDir(f, pkg);
            } else if (f.getName().endsWith("html") || f.getName().endsWith("htm")) {
                readHtml(f, root);
            }
        }
    }

    private void readHtml(File f, MPackage root) {
        try {
            String fileName = f.getName().toLowerCase();
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            MPackage pkg = root.findOrCreatePackage(fileName.toLowerCase());
            Document page = Jsoup.parse(f);
            for (FormElement form : page.forms()) {
                addFormBean(pkg, form);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addFormBean(MPackage pkg, FormElement form) {
        String clazzName = toClassName(form);
        if (clazzName == null) {
            return;
        }
        MClass formBean = pkg.createMClass(clazzName);
        formBean.addStereotypes(new StereotypeImpl("FormBean"));
        formBean.setModel(pkg.getModel());
        addAttributes(form, formBean);
    }

    private void addAttributes(FormElement form, MClass formBean) {
        for (Element e : form.elements()) {
            if( e.tagName().equals("input") ) {
                handleInputTag(formBean, e);
            } else if( e.tagName().equals("textarea") ) {
                handleInputTag(formBean, e);
            } else if( e.tagName().equals("select") ) {
                handleSelect(formBean, e);
            } else if(e.tagName().equals("button") ) {
                handleButton(formBean, e);
            }
        }
    }

    private void handleButton(MClass formBean, Element e) {

    }

    private void handleSelect(MClass formBean, Element e) {
        handleInputTag(formBean, e);
        String name = e.attr("name");
        if( StringUtils.isEmpty(name) ) {
            name = e.attr("id");
        }
        if( name.isEmpty() ) {
            return;
        }
        name = NamingStrategy.toClassName(name);
        MClass valueEnum = formBean.getPackage().createMClass("Select"+name+"OptionEnum");
        valueEnum.addStereotypes(new StereotypeImpl("SelectOptionEnum"));
        for( Element value : e.children() ) {
            if( value.tagName().equals("option") ) {
                String optValue = value.attr("value");
                String optDisplay = value.text();
                String javaName = NamingStrategy.toJavaName(optDisplay);
                MAttribute a = valueEnum.createAttribute(javaName, "String");
                StereotypeImpl sType = new StereotypeImpl("OptionEnumEntry");
                sType.setTaggedValue("value", optValue);
                sType.setTaggedValue("display", optDisplay);
                a.addStereotypes(sType);
            }
        }
    }

    private void handleInputTag(MClass formBean, Element e) {
        String xPath;
        String id = e.attr("id");
        String name= e.attr("name");
        if( StringUtils.isEmpty(name) ) {
            name = id;
        }
        if( StringUtils.isEmpty(name) ) {
            return;
        }
        if( !StringUtils.isEmpty(id) ) {
             xPath = String.format("//"+e.tagName()+"[@id='%s']", id);
        } else {
            xPath = String.format("//"+e.tagName()+"[@name='%s']", name);
        }
        String type = "FormFieldType."+e.tagName().toUpperCase();
        if( !StringUtils.isEmpty(e.attr("type")) ) {
            type = "FormFieldType."+e.attr("type").toUpperCase();
        }
        StereotypeImpl sType = new StereotypeImpl("FormField");
        sType.setTaggedValue("xpath", xPath);
        sType.setTaggedValue("type", type);
        MAttribute attr = formBean.createAttribute(NamingStrategy.toJavaName(name), "String");
        attr.addStereotypes(sType);
    }

    private String toClassName(FormElement form) {
        String id = form.attr("id");
        String name = form.attr("name");
        if (!StringUtils.isEmpty(id)) {
            return NamingStrategy.toClassName(id);
        } else if (!StringUtils.isEmpty(name)) {
            return NamingStrategy.toClassName(name);
        }
        return null;
    }
}
