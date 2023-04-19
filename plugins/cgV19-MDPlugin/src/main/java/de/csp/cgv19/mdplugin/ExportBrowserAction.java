package de.csp.cgv19.mdplugin;

import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import de.csp.cgv19.mdplugin.oom.OOMExporter;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ExportBrowserAction extends DefaultBrowserAction {
    public ExportBrowserAction() {
        super("", "Export cgV19-Model", null, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Tree tree = getTree();
        String text = "Selected elements:";
        for (int i = 0; i < tree.getSelectedNodes().length; i++) {
            Node node = tree.getSelectedNodes()[i];
            Object userObject = node.getUserObject();
            if (userObject instanceof BaseElement) {
                BaseElement element = (BaseElement) userObject;
                OOMExporter.export(element);
            }
        }
    }

    @Override
    public void updateState() {
        Tree tree = getTree();
        if (tree != null) {
            boolean enabled = true;
            enabled &= tree.getSelectedNode() != null;
            Node n = tree.getSelectedNode();
            Object userObject = n.getUserObject();
            if (userObject instanceof Element) {
                Element e = (Element)userObject;
                    enabled &= StereotypesHelper.hasStereotype(e, "cgV19Project");
            } else {
                enabled = false;
            }
            setEnabled(enabled);
        }
    }
}
